package com.skillsync.session.service.impl;

import com.skillsync.session.client.MentorServiceClient;
import com.skillsync.session.client.SkillServiceClient;
import com.skillsync.session.client.UserServiceClient;
import com.skillsync.session.dto.SessionEvent;
import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.entity.MentoringSession;
import com.skillsync.session.exception.BadRequestException;
import com.skillsync.session.exception.ResourceNotFoundException;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.SessionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final UserServiceClient userServiceClient;
    private final MentorServiceClient mentorServiceClient;
    private final SkillServiceClient skillServiceClient;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.session}")
    private String sessionRoutingKey;

    private static final Set<String> VALID_STATUSES =
            Set.of("SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

    @Override
    public SessionResponse createSession(SessionRequest request) {
        log.info("Creating session between mentor {} and mentee {}", request.getMentorId(), request.getMenteeId());

        if (request.getEndTime() != null && request.getStartTime() != null
                && !request.getEndTime().isAfter(request.getStartTime())) {
            throw new BadRequestException("End time must be after start time");
        }

        try {
            userServiceClient.getUserByUserId(request.getMenteeId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Mentee (User) not found with id: " + request.getMenteeId());
        }

        try {
            mentorServiceClient.getMentorById(request.getMentorId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Mentor not found with id: " + request.getMentorId());
        }

        try {
            skillServiceClient.getSkillById(request.getSkillId());
        } catch (FeignException.NotFound e) {
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

        MentoringSession saved = sessionRepository.save(session);
        publishSessionEvent(saved);
        return mapToResponse(saved);
    }

    @Override
    public List<SessionResponse> getSessionsByMentorId(Long mentorId) {
        return sessionRepository.findByMentorId(mentorId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<SessionResponse> getSessionsByMenteeId(Long menteeId) {
        return sessionRepository.findByMenteeId(menteeId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public SessionResponse getSessionById(Long id) {
        return mapToResponse(sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id)));
    }

    @Override
    public SessionResponse updateSessionStatus(Long id, String status) {
        if (status == null || !VALID_STATUSES.contains(status.toUpperCase())) {
            throw new BadRequestException("Invalid status '" + status + "'. Allowed: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED");
        }
        MentoringSession session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with id: " + id));
        session.setStatus(status.toUpperCase());
        MentoringSession updated = sessionRepository.save(session);
        publishSessionEvent(updated);
        return mapToResponse(updated);
    }

    private void publishSessionEvent(MentoringSession session) {
        Long mentorUserId = null;
        try {
            Map<String, Object> mentorData = mentorServiceClient.getMentorById(session.getMentorId());
            if (mentorData.get("userId") instanceof Number n) {
                mentorUserId = n.longValue();
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

    private SessionResponse mapToResponse(MentoringSession s) {
        return SessionResponse.builder()
                .id(s.getId()).mentorId(s.getMentorId()).menteeId(s.getMenteeId())
                .skillId(s.getSkillId()).startTime(s.getStartTime()).endTime(s.getEndTime())
                .status(s.getStatus()).meetingLink(s.getMeetingLink()).build();
    }

    private String generateMeetingLink() {
        return "https://meet.skillsync.com/" + UUID.randomUUID().toString().substring(0, 8);
    }
}
