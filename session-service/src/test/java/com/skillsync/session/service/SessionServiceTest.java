package com.skillsync.session.service;

import com.skillsync.session.client.MentorServiceClient;
import com.skillsync.session.client.SkillServiceClient;
import com.skillsync.session.client.UserServiceClient;
import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.entity.MentoringSession;
import com.skillsync.session.exception.ResourceNotFoundException;
import com.skillsync.session.repository.SessionRepository;
import com.skillsync.session.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock private SessionRepository sessionRepository;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private UserServiceClient userServiceClient;
    @Mock private MentorServiceClient mentorServiceClient;
    @Mock private SkillServiceClient skillServiceClient;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessionService, "exchange", "skillsync.exchange");
        ReflectionTestUtils.setField(sessionService, "sessionRoutingKey", "session.event");
    }

    @Test
    void createSessionSetsScheduledStatusAndPublishesEvent() {
        SessionRequest request = new SessionRequest();
        request.setMentorId(1L);
        request.setMenteeId(2L);
        request.setSkillId(3L);
        request.setStartTime(LocalDateTime.of(2027, 3, 24, 10, 0));
        request.setEndTime(LocalDateTime.of(2027, 3, 24, 11, 0));

        when(userServiceClient.getUserByUserId(2L)).thenReturn(Map.of("id", 2));
        when(mentorServiceClient.getMentorById(1L)).thenReturn(Map.of("id", 1, "userId", 10));
        when(skillServiceClient.getSkillById(3L)).thenReturn(Map.of("id", 3));
        when(sessionRepository.save(any(MentoringSession.class))).thenAnswer(inv -> {
            MentoringSession s = inv.getArgument(0);
            s.setId(9L);
            return s;
        });

        SessionResponse response = sessionService.createSession(request);

        assertNotNull(response);
        assertEquals("SCHEDULED", response.getStatus());
        assertEquals(1L, response.getMentorId());
        assertEquals(2L, response.getMenteeId());
        verify(rabbitTemplate).convertAndSend(eq("skillsync.exchange"), eq("session.event"), any(com.skillsync.session.dto.SessionEvent.class));
    }

    @Test
    void createSessionThrowsWhenMenteeNotFound() {
        SessionRequest request = new SessionRequest();
        request.setMentorId(1L);
        request.setMenteeId(99L);
        request.setSkillId(3L);
        request.setStartTime(LocalDateTime.of(2027, 3, 24, 10, 0));
        request.setEndTime(LocalDateTime.of(2027, 3, 24, 11, 0));

        when(userServiceClient.getUserByUserId(99L))
                .thenThrow(feign.FeignException.NotFound.class);

        assertThrows(ResourceNotFoundException.class, () -> sessionService.createSession(request));
    }
}
