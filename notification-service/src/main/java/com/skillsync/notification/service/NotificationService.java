package com.skillsync.notification.service;

import com.skillsync.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    NotificationResponse createNotification(Long userId, String type, String message);
    NotificationResponse createNotificationWithEmail(Long userId, String type, String message, String toEmail);
    List<NotificationResponse> getUnreadNotifications(Long userId);
    List<NotificationResponse> getAllNotifications(Long userId);
    NotificationResponse markAsRead(Long notificationId);
}
