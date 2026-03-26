package com.skillsync.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResponse {
    private Long id;
    private Long mentorId;
    private Long menteeId;
    private Long skillId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String meetingLink;
}
