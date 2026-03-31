package com.skillsync.notification.listener;

import com.skillsync.notification.client.UserServiceClient;
import com.skillsync.notification.dto.SessionEvent;
import com.skillsync.notification.exception.InvalidEventException;
import com.skillsync.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionEventListener {

    private final NotificationService notificationService;
    private final UserServiceClient userServiceClient;

    @RabbitListener(queues = "${rabbitmq.queue.session}")
    public void handleSessionEvent(SessionEvent event) {
        if (event == null || event.getSessionId() == null) {
            throw new InvalidEventException("Received null or invalid session event");
        }
        if (event.getStatus() == null || event.getStatus().isBlank()) {
            throw new InvalidEventException("Session event missing status for sessionId: " + event.getSessionId());
        }

        log.info("Received session event: sessionId={}, status={}", event.getSessionId(), event.getStatus());

        String type = "SESSION_" + event.getStatus();

        // Notify mentee
        if (event.getMenteeId() != null) {
            String email = fetchEmail(event.getMenteeId());
            String message = buildMenteeMessage(event);
            String subject = buildSubject(event.getStatus());
            if (email != null) {
                notificationService.createNotificationWithEmail(event.getMenteeId(), type, message, email, subject);
            } else {
                notificationService.createNotification(event.getMenteeId(), type, message);
            }
        }

        // Notify mentor
        if (event.getMentorUserId() != null) {
            String email = fetchEmail(event.getMentorUserId());
            String message = buildMentorMessage(event);
            String subject = buildSubject(event.getStatus());
            if (email != null) {
                notificationService.createNotificationWithEmail(event.getMentorUserId(), type, message, email, subject);
            } else {
                notificationService.createNotification(event.getMentorUserId(), type, message);
            }
        }
    }

    private String fetchEmail(Long userId) {
        try {
            Object email = userServiceClient.getUserByUserId(userId).get("email");
            return email != null ? email.toString() : null;
        } catch (Exception e) {
            log.warn("Could not fetch email for userId {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private String buildSubject(String status) {
        return switch (status) {
            case "SCHEDULED"  -> "[SkillSync] Your session has been scheduled";
            case "CANCELLED"  -> "[SkillSync] Your session has been cancelled";
            case "COMPLETED"  -> "[SkillSync] Your session is complete";
            case "IN_PROGRESS" -> "[SkillSync] Your session is now in progress";
            default -> "[SkillSync] Session status update";
        };
    }

    private String buildMenteeMessage(SessionEvent event) {
        return switch (event.getStatus()) {
            case "SCHEDULED" -> """
                    Hi there,

                    Great news! Your mentoring session has been successfully scheduled.

                    Session Details:
                    - Session ID : #%d
                    - Status     : Scheduled

                    Please be ready at the scheduled time. Your meeting link will be available in the session details.

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            case "CANCELLED" -> """
                    Hi there,

                    We're sorry to inform you that your mentoring session has been cancelled.

                    Session Details:
                    - Session ID : #%d
                    - Status     : Cancelled

                    You can book a new session anytime through the SkillSync platform.

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            case "COMPLETED" -> """
                    Hi there,

                    Your mentoring session has been completed successfully!

                    Session Details:
                    - Session ID : #%d
                    - Status     : Completed

                    We hope it was a valuable experience. Don't forget to leave a review for your mentor!

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            default -> "Your session #%d status has been updated to: %s".formatted(event.getSessionId(), event.getStatus());
        };
    }

    private String buildMentorMessage(SessionEvent event) {
        return switch (event.getStatus()) {
            case "SCHEDULED" -> """
                    Hi Mentor,

                    A new mentoring session has been scheduled with you.

                    Session Details:
                    - Session ID : #%d
                    - Status     : Scheduled

                    Please be prepared and available at the scheduled time.

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            case "CANCELLED" -> """
                    Hi Mentor,

                    A mentoring session scheduled with you has been cancelled.

                    Session Details:
                    - Session ID : #%d
                    - Status     : Cancelled

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            case "COMPLETED" -> """
                    Hi Mentor,

                    Your mentoring session has been marked as completed.

                    Session Details:
                    - Session ID : #%d
                    - Status     : Completed

                    Thank you for your contribution to the SkillSync community!

                    Best regards,
                    The SkillSync Team
                    """.formatted(event.getSessionId());
            default -> "Session #%d status updated to: %s".formatted(event.getSessionId(), event.getStatus());
        };
    }
}
