package com.skillsync.session.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvent {
    private Long sessionId;
    private Long mentorId;
    private Long mentorUserId;
    private Long menteeId;
    private String status;
}
