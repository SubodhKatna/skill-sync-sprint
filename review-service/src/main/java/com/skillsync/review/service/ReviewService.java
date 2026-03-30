package com.skillsync.review.service;

import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);
    List<ReviewResponse> getReviewsByMentorId(Long mentorId);
    ReviewResponse getReviewById(Long id);
}
