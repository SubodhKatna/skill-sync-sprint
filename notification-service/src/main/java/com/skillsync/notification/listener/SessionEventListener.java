package com.skillsync.notification.listener;

import com.skillsync.notification.dto.SessionEvent;
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

    @RabbitListener(queues = "${rabbitmq.queue.session}")
    public void handleSessionEvent(SessionEvent event) {
        log.info("Received session event: sessionId={}, status={}", event.getSessionId(), event.getStatus());

        String type = "SESSION_" + event.getStatus();
        String message = buildSessionMessage(event);

        // Notify the mentee
        if (event.getMenteeId() != null) {
            notificationService.createNotification(event.getMenteeId(), type, message);
        }

        // Notify the mentor
        if (event.getMentorId() != null) {
            notificationService.createNotification(event.getMentorId(), type, message);
        }
    }

    private String buildSessionMessage(SessionEvent event) {
        return switch (event.getStatus()) {
            case "SCHEDULED" -> "Session #" + event.getSessionId() + " has been scheduled.";
            case "CANCELLED" -> "Session #" + event.getSessionId() + " has been cancelled.";
            case "COMPLETED" -> "Session #" + event.getSessionId() + " has been completed.";
            default -> "Session #" + event.getSessionId() + " status updated to: " + event.getStatus();
        };
    }
}
