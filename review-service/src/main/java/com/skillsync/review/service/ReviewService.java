package com.skillsync.review.service;

import com.skillsync.review.dto.ReviewRequest;
import com.skillsync.review.entity.Review;

import java.util.List;

public interface ReviewService {
    Review createReview(ReviewRequest request);
    List<Review> getReviewsByMentorId(Long mentorId);
    Review getReviewById(Long id);
}
