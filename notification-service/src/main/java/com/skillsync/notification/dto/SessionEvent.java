package com.skillsync.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionEvent {
    private Long sessionId;
    private Long mentorId;
    private Long mentorUserId;
    private Long menteeId;
    private String status;
}
