package com.skillsync.review.service.impl;

import com.skillsync.review.client.MentorServiceClient;
import com.skillsync.review.client.SessionServiceClient;
import com.skillsync.review.dto.ReviewEvent;
import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.dto.ReviewResponse;
import com.skillsync.review.entity.Review;
import com.skillsync.review.exception.BadRequestException;
import com.skillsync.review.exception.ConflictException;
import com.skillsync.review.exception.ResourceNotFoundException;
import com.skillsync.review.repository.ReviewRepository;
import com.skillsync.review.service.ReviewService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final RabbitTemplate rabbitTemplate;
    private final SessionServiceClient sessionServiceClient;
    private final MentorServiceClient mentorServiceClient;

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.routingkey.review}")
    private String reviewRoutingKey;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        if (reviewRepository.existsBySessionIdAndReviewerId(request.getSessionId(), request.getReviewerId())) {
            throw new ConflictException("Review already submitted for session " + request.getSessionId()
                    + " by reviewer " + request.getReviewerId());
        }

        try {
            sessionServiceClient.getSessionById(request.getSessionId());
        } catch (FeignException.NotFound e) {
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

        updateMentorRating(request.getMentorId());
        publishReviewEvent(saved);

        return new ReviewResponse(saved);
    }

    @Override
    public List<ReviewResponse> getReviewsByMentorId(Long mentorId) {
        return reviewRepository.findByMentorId(mentorId).stream().map(ReviewResponse::new).toList();
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        return new ReviewResponse(reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id)));
    }

    private void updateMentorRating(Long mentorId) {
        List<Review> reviews = reviewRepository.findByMentorId(mentorId);
        if (reviews.isEmpty()) return;
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
        try {
            mentorServiceClient.updateRating(mentorId, Map.of("rating", avg, "totalReviews", reviews.size()));
            log.info("Updated mentor {} rating to {}", mentorId, avg);
        } catch (Exception e) {
            log.warn("Failed to update mentor {} rating: {}", mentorId, e.getMessage());
        }
    }

    private void publishReviewEvent(Review review) {
        Long mentorUserId = null;
        try {
            Map<String, Object> mentorData = mentorServiceClient.getMentorById(review.getMentorId());
            if (mentorData.get("userId") instanceof Number n) {
                mentorUserId = n.longValue();
            }
        } catch (Exception e) {
            log.warn("Could not fetch mentor userId for mentorId: {}", review.getMentorId());
        }

        ReviewEvent event = ReviewEvent.builder()
                .reviewId(review.getId()).mentorId(review.getMentorId())
                .mentorUserId(mentorUserId).reviewerId(review.getReviewerId())
                .rating((double) review.getRating()).sessionId(review.getSessionId())
                .build();

        rabbitTemplate.convertAndSend(exchange, reviewRoutingKey, event);
        log.info("Published review event for review ID: {}", review.getId());
    }
}
