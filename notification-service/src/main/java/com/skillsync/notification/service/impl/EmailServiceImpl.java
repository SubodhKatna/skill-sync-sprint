package com.skillsync.notification.service.impl;

import com.skillsync.notification.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailServiceImpl(JavaMailSender mailSender,
                            @Value("${spring.mail.from:noreply@skillsync.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendNotificationEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            log.warn("Failed to send email to {}: {}", toEmail, e.getMessage());
        }
    }
}
