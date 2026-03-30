package com.skillsync.session.service.impl;

import com.skillsync.session.dto.SessionEvent;
import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.entity.MentoringSession;
import com.skillsync.session.exception.BadRequestException;
import com.skillsync.session.exception.ResourceNotFoundException;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.session}")
    private String sessionRoutingKey;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${mentor.service.url}")
    private String mentorServiceUrl;

    @Value("${skill.service.url}")
    private String skillServiceUrl;

    private final RestTemplate restTemplate;

    private static final java.util.Set<String> VALID_STATUSES =
            java.util.Set.of("SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    @Override
    public SessionResponse createSession(SessionRequest request) {
        log.info("Creating session between mentor {} and mentee {}", request.getMentorId(), request.getMenteeId());

        if (request.getEndTime() != null && request.getStartTime() != null
                && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        // Validate Mentee by userId
        try {
            restTemplate.getForObject(userServiceUrl + "/users/by-user/" + request.getMenteeId(), java.util.Map.class);
        } catch (Exception e) {
            log.error("Mentee validation failed for userId: {}", request.getMenteeId(), e);
            throw new ResourceNotFoundException("Mentee (User) not found with id: " + request.getMenteeId());
        }

        // Validate Mentor
        try {
            restTemplate.getForObject(mentorServiceUrl + "/mentors/" + request.getMentorId(), java.util.Map.class);
        } catch (Exception e) {
            log.error("Mentor validation failed for mentorId: {}", request.getMentorId(), e);
            throw new ResourceNotFoundException("Mentor not found with id: " + request.getMentorId());
        }

        // Validate Skill
        try {
            restTemplate.getForObject(skillServiceUrl + "/skills/" + request.getSkillId(), java.util.Map.class);
        } catch (Exception e) {
            log.error("Skill validation failed for skillId: {}", request.getSkillId(), e);
            throw new ResourceNotFoundException("Skill not found with id: " + request.getSkillId());
        }

        MentoringSession session = MentoringSession.builder()
                .mentorId(request.getMentorId())
                .menteeId(request.getMenteeId())
                .skillId(request.getSkillId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("SCHEDULED")
                .meetingLink(request.getMeetingLink() != null ? request.getMeetingLink() : generateMeetingLink())
                .build();

        MentoringSession savedSession = sessionRepository.save(session);

        publishSessionEvent(savedSession);

        return mapToResponse(savedSession);
    }

    @Override
    public List<SessionResponse> getSessionsByMentorId(Long mentorId) {
        return sessionRepository.findByMentorId(mentorId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getSessionsByMenteeId(Long menteeId) {
        return sessionRepository.findByMenteeId(menteeId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SessionResponse getSessionById(Long id) {
        MentoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
        return mapToResponse(session);
    }

    @Override
    public SessionResponse updateSessionStatus(Long id, String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new BadRequestException("Invalid status '" + status + "'. Allowed values: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED");
        }
        MentoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));

        session.setStatus(status.toUpperCase());
        MentoringSession updatedSession = sessionRepository.save(session);
        
        publishSessionEvent(updatedSession);
        
        return mapToResponse(updatedSession);
    }

    private void publishSessionEvent(MentoringSession session) {
        Long mentorUserId = null;
        try {
            java.util.Map<?, ?> mentorData = restTemplate.getForObject(
                    mentorServiceUrl + "/mentors/" + session.getMentorId(), java.util.Map.class);
            if (mentorData != null && mentorData.get("userId") instanceof Number) {
                mentorUserId = ((Number) mentorData.get("userId")).longValue();
            }
        } catch (Exception e) {
            log.warn("Could not fetch mentor userId for mentorId: {}", session.getMentorId());
        }

        SessionEvent event = SessionEvent.builder()
                .sessionId(session.getId())
                .mentorId(session.getMentorId())
                .mentorUserId(mentorUserId)
                .menteeId(session.getMenteeId())
                .status(session.getStatus())
                .build();

        rabbitTemplate.convertAndSend(exchange, sessionRoutingKey, event);
        log.info("Published session event for session ID: {}", session.getId());
    }

    private SessionResponse mapToResponse(MentoringSession session) {
        return SessionResponse.builder()
                .id(session.getId())
                .mentorId(session.getMentorId())
                .menteeId(session.getMenteeId())
                .skillId(session.getSkillId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .status(session.getStatus())
                .meetingLink(session.getMeetingLink())
                .build();
    }

    private String generateMeetingLink() {
        return "https://meet.skillsync.com/" + java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}
