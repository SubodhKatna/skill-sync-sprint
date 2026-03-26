package com.skillsync.notification.service;

import com.skillsync.notification.entity.Notification;

import java.util.List;

public interface NotificationService {
    Notification createNotification(Long userId, String type, String message);
    List<Notification> getUnreadNotifications(Long userId);
    List<Notification> getAllNotifications(Long userId);
    Notification markAsRead(Long notificationId);
}
