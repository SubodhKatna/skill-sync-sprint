package com.skillsync.notification.service.impl;

import com.skillsync.notification.dto.NotificationResponse;
import com.skillsync.notification.entity.Notification;
import com.skillsync.notification.exception.NotificationAlreadyReadException;
import com.skillsync.notification.exception.ResourceNotFoundException;
import com.skillsync.notification.repository.NotificationRepository;
import com.skillsync.notification.service.EmailService;
import com.skillsync.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Override
    public NotificationResponse createNotification(Long userId, String type, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setRead(false);
        return new NotificationResponse(notificationRepository.save(notification));
    }

    @Override
    public NotificationResponse createNotificationWithEmail(Long userId, String type, String message, String toEmail, String subject) {
        NotificationResponse response = createNotification(userId, type, message);
        emailService.sendNotificationEmail(toEmail, subject, message);
        return response;
    }

    @Override
    public List<NotificationResponse> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId)
                .stream().map(NotificationResponse::new).toList();
    }

    @Override
    public List<NotificationResponse> getAllNotifications(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream().map(NotificationResponse::new).toList();
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + notificationId));
        if (notification.isRead()) {
            throw new NotificationAlreadyReadException("Notification " + notificationId + " is already marked as read");
        }
        notification.setRead(true);
        return new NotificationResponse(notificationRepository.save(notification));
    }
}
