package com.skillsync.session.service;

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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private SessionServiceImpl sessionService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sessionService, "exchange", "skillsync.exchange");
        ReflectionTestUtils.setField(sessionService, "sessionRoutingKey", "session.event");
        ReflectionTestUtils.setField(sessionService, "userServiceUrl", "http://user-service:8082");
        ReflectionTestUtils.setField(sessionService, "mentorServiceUrl", "http://mentor-service:8084");
        ReflectionTestUtils.setField(sessionService, "skillServiceUrl", "http://skill-service:8083");
    }

    @Test
    void createSessionSetsScheduledStatusAndPublishesEvent() {
        SessionRequest request = new SessionRequest();
        request.setMentorId(1L);
        request.setMenteeId(2L);
        request.setSkillId(3L);
        request.setStartTime(LocalDateTime.of(2027, 3, 24, 10, 0));
        request.setEndTime(LocalDateTime.of(2027, 3, 24, 11, 0));

        // Mock inter-service validations to succeed
        when(restTemplate.getForObject(anyString(), eq(java.util.Map.class))).thenReturn(java.util.Map.of("id", 1));

        when(sessionRepository.save(any(MentoringSession.class))).thenAnswer(invocation -> {
            MentoringSession saved = invocation.getArgument(0);
            saved.setId(9L);
            return saved;
        });

        SessionResponse response = sessionService.createSession(request);

        assertNotNull(response);
        assertEquals("SCHEDULED", response.getStatus());
        assertEquals(1L, response.getMentorId());
        assertEquals(2L, response.getMenteeId());

        verify(rabbitTemplate).convertAndSend(
                eq("skillsync.exchange"),
                eq("session.event"),
                any(Object.class));
    }

    @Test
    void createSessionThrowsWhenMenteeNotFound() {
        SessionRequest request = new SessionRequest();
        request.setMentorId(1L);
        request.setMenteeId(99L);
        request.setSkillId(3L);
        request.setStartTime(LocalDateTime.of(2027, 3, 24, 10, 0));
        request.setEndTime(LocalDateTime.of(2027, 3, 24, 11, 0));

        // Simulate mentee not found
        when(restTemplate.getForObject(contains("user-service"), eq(java.util.Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> sessionService.createSession(request));
    }
}
