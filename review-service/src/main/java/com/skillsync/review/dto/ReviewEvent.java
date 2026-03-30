package com.skillsync.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    private Long reviewId;
    private Long mentorId;
    private Long mentorUserId;
    private Long reviewerId;
    private Double rating;
    private Long sessionId;
}
