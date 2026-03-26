package com.skillsync.notification.listener;

import com.skillsync.notification.dto.ReviewEvent;
import com.skillsync.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${rabbitmq.queue.review}")
    public void handleReviewEvent(ReviewEvent event) {
        log.info("Received review event: reviewId={}, mentorId={}", event.getReviewId(), event.getMentorId());

        String message = "You received a new review (rating: " + event.getRating() + "/5) for session #" + event.getSessionId();

        // Notify the mentor about the new review
        if (event.getMentorId() != null) {
            notificationService.createNotification(event.getMentorId(), "REVIEW_RECEIVED", message);
        }
    }
}
