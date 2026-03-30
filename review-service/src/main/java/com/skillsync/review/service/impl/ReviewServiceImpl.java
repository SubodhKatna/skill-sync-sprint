package com.skillsync.review.service.impl;

import com.skillsync.review.dto.ReviewEvent;
import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.entity.Review;
import com.skillsync.review.exception.BadRequestException;
import com.skillsync.review.exception.ConflictException;
import com.skillsync.review.exception.ResourceNotFoundException;
import com.skillsync.review.repository.ReviewRepository;
import com.skillsync.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RestTemplate restTemplate;

    @Value("${session.service.url}")
    private String sessionServiceUrl;

    @Value("${mentor.service.url}")
    private String mentorServiceUrl;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.review}")
    private String reviewRoutingKey;

    @Override
    public Review createReview(ReviewRequest request) {
        // Prevent duplicate reviews from the same reviewer for the same session
        if (reviewRepository.existsBySessionIdAndReviewerId(request.getSessionId(), request.getReviewerId())) {
            throw new ConflictException("Review already submitted for session " + request.getSessionId() + " by reviewer " + request.getReviewerId());
        }

        // Validate session exists in session-service
        try {
            restTemplate.getForObject(sessionServiceUrl + "/api/v1/sessions/" + request.getSessionId(), Map.class);
        } catch (Exception e) {
            log.error("Session validation failed for sessionId: {}", request.getSessionId(), e);
            throw new ResourceNotFoundException("Session not found with id: " + request.getSessionId());
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        Review review = new Review();
        review.setSessionId(request.getSessionId());
        review.setMentorId(request.getMentorId());
        review.setReviewerId(request.getReviewerId());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review saved = reviewRepository.save(review);

        // Update mentor rating in mentor-service
        updateMentorRating(request.getMentorId());

        // Publish review event to notification-service via RabbitMQ
        publishReviewEvent(saved);

        return saved;
    }

    @Override
    public List<Review> getReviewsByMentorId(Long mentorId) {
        return reviewRepository.findByMentorId(mentorId);
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
    }

    private void updateMentorRating(Long mentorId) {
        List<Review> mentorReviews = reviewRepository.findByMentorId(mentorId);
        if (mentorReviews.isEmpty()) return;

        double avgRating = mentorReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        int totalReviews = mentorReviews.size();

        try {
            Map<String, Object> ratingUpdate = Map.of(
                    "rating", avgRating,
                    "totalReviews", totalReviews
            );
            restTemplate.put(mentorServiceUrl + "/mentors/" + mentorId + "/rating", ratingUpdate);
            log.info("Updated mentor {} rating to {} ({} reviews)", mentorId, avgRating, totalReviews);
        } catch (Exception e) {
            log.warn("Failed to update mentor {} rating: {}", mentorId, e.getMessage());
        }
    }

    private void publishReviewEvent(Review review) {
        Long mentorUserId = null;
        try {
            Map<?, ?> mentorData = restTemplate.getForObject(
                    mentorServiceUrl + "/mentors/" + review.getMentorId(), Map.class);
            if (mentorData != null && mentorData.get("userId") instanceof Number) {
                mentorUserId = ((Number) mentorData.get("userId")).longValue();
            }
        } catch (Exception e) {
            log.warn("Could not fetch mentor userId for mentorId: {}", review.getMentorId());
        }

        ReviewEvent event = ReviewEvent.builder()
                .reviewId(review.getId())
                .mentorId(review.getMentorId())
                .mentorUserId(mentorUserId)
                .reviewerId(review.getReviewerId())
                .rating((double) review.getRating())
                .sessionId(review.getSessionId())
                .build();

        rabbitTemplate.convertAndSend(exchange, reviewRoutingKey, event);
        log.info("Published review event for review ID: {}", review.getId());
    }
}
