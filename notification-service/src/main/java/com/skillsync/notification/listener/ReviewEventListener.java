package com.skillsync.notification.listener;

import com.skillsync.notification.client.UserServiceClient;
import com.skillsync.notification.dto.ReviewEvent;
import com.skillsync.notification.exception.InvalidEventException;
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
    private final UserServiceClient userServiceClient;

    @RabbitListener(queues = "${rabbitmq.queue.review}")
    public void handleReviewEvent(ReviewEvent event) {
        if (event == null || event.getReviewId() == null) {
            throw new InvalidEventException("Received null or invalid review event");
        }

        log.info("Received review event: reviewId={}, mentorId={}, rating={}", 
                event.getReviewId(), event.getMentorId(), event.getRating());

        if (event.getMentorUserId() != null) {
            String email = fetchEmail(event.getMentorUserId());
            String message = buildMentorMessage(event);
            if (email != null) {
                notificationService.createNotificationWithEmail(
                        event.getMentorUserId(), "REVIEW_RECEIVED", message, email,
                        "[SkillSync] You received a new review!");
            } else {
                notificationService.createNotification(
                        event.getMentorUserId(), "REVIEW_RECEIVED", message);
            }
        }

        // Notify reviewer their review was submitted
        if (event.getReviewerId() != null) {
            String email = fetchEmail(event.getReviewerId());
            String message = buildReviewerMessage(event);
            if (email != null) {
                notificationService.createNotificationWithEmail(
                        event.getReviewerId(), "REVIEW_SUBMITTED", message, email,
                        "[SkillSync] Your review has been submitted");
            } else {
                notificationService.createNotification(
                        event.getReviewerId(), "REVIEW_SUBMITTED", message);
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

    private String buildMentorMessage(ReviewEvent event) {
        String stars = "★".repeat(event.getRating().intValue()) + "☆".repeat(5 - event.getRating().intValue());
        return """
                Hi Mentor,

                You have received a new review on SkillSync!

                Review Details:
                - Review ID  : #%d
                - Session ID : #%d
                - Rating     : %s (%.1f/5)

                Keep up the great work and continue inspiring learners!

                Best regards,
                The SkillSync Team
                """.formatted(event.getReviewId(), event.getSessionId(), stars, event.getRating());
    }

    private String buildReviewerMessage(ReviewEvent event) {
        return """
                Hi there,

                Your review has been successfully submitted. Thank you for your feedback!

                Review Details:
                - Review ID  : #%d
                - Session ID : #%d
                - Rating     : %.1f/5

                Your feedback helps mentors improve and grow.

                Best regards,
                The SkillSync Team
                """.formatted(event.getReviewId(), event.getSessionId(), event.getRating());
    }
}
