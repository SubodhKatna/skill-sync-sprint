package com.skillsync.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvent {
    private Long reviewId;
    private Long mentorId;
    private Long reviewerId;
    private Double rating;
    private Long sessionId;
}
