package com.skillsync.notification.service;

public interface EmailService {
    void sendNotificationEmail(String toEmail, String subject, String body);
}
