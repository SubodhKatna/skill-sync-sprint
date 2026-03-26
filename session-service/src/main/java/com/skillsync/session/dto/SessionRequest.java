package com.skillsync.session.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SessionRequest {
    @NotNull(message = "Mentor ID is required")
    private Long mentorId;

    @NotNull(message = "Mentee ID is required")
    private Long menteeId;

    @NotNull(message = "Skill ID is required")
    private Long skillId;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    
    private String meetingLink;
}
