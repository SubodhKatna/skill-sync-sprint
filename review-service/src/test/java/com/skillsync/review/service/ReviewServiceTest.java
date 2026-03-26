package com.skillsync.review.service;

import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.entity.Review;
import com.skillsync.review.exception.ConflictException;
import com.skillsync.review.exception.ResourceNotFoundException;
import com.skillsync.review.repository.ReviewRepository;
import com.skillsync.review.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(reviewService, "sessionServiceUrl", "http://session-service:8085");
        ReflectionTestUtils.setField(reviewService, "mentorServiceUrl", "http://mentor-service:8084");
        ReflectionTestUtils.setField(reviewService, "exchange", "skillsync.exchange");
        ReflectionTestUtils.setField(reviewService, "reviewRoutingKey", "review.event");
    }

    @Test
    void createReviewSavesAndPublishesEvent() {
        ReviewRequest request = new ReviewRequest();
        request.setSessionId(1L);
        request.setMentorId(2L);
        request.setReviewerId(3L);
        request.setRating(5);
        request.setComment("Excellent session!");

        when(reviewRepository.existsBySessionIdAndReviewerId(1L, 3L)).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(Map.of("id", 1));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(10L);
            return r;
        });
        when(reviewRepository.findByMentorId(2L)).thenReturn(List.of());

        Review result = reviewService.createReview(request);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(rabbitTemplate).convertAndSend(eq("skillsync.exchange"), eq("review.event"), any(Object.class));
    }

    @Test
    void createReviewThrowsConflictOnDuplicate() {
        ReviewRequest request = new ReviewRequest();
        request.setSessionId(1L);
        request.setReviewerId(3L);
        request.setMentorId(2L);
        request.setRating(4);

        when(reviewRepository.existsBySessionIdAndReviewerId(1L, 3L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> reviewService.createReview(request));
    }

    @Test
    void createReviewThrowsWhenSessionNotFound() {
        ReviewRequest request = new ReviewRequest();
        request.setSessionId(99L);
        request.setMentorId(2L);
        request.setReviewerId(3L);
        request.setRating(3);

        when(reviewRepository.existsBySessionIdAndReviewerId(99L, 3L)).thenReturn(false);
        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenThrow(new org.springframework.web.client.HttpClientErrorException(
                        org.springframework.http.HttpStatus.NOT_FOUND));

        assertThrows(ResourceNotFoundException.class, () -> reviewService.createReview(request));
    }
}
