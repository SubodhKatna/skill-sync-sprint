package com.skillsync.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillsync.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {

    private final Long id;
    private final Long userId;
    private final String type;
    private final String message;

    @JsonProperty("isRead")
    private final boolean read;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public NotificationResponse(Notification n) {
        this.id = n.getId();
        this.userId = n.getUserId();
        this.type = n.getType();
        this.message = n.getMessage();
        this.read = n.isRead();
        this.createdAt = n.getCreatedAt();
    }
}
