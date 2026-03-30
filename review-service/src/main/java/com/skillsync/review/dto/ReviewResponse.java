package com.skillsync.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.review.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private final Long id;
    private final Long sessionId;
    private final Long mentorId;
    private final Long reviewerId;
    private final Integer rating;
    private final String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public ReviewResponse(Review r) {
        this.id = r.getId();
        this.sessionId = r.getSessionId();
        this.mentorId = r.getMentorId();
        this.reviewerId = r.getReviewerId();
        this.rating = r.getRating();
        this.comment = r.getComment();
        this.createdAt = r.getCreatedAt();
    }
}
