package com.skillsync.notification.service;

import com.skillsync.notification.entity.Notification;
import com.skillsync.notification.exception.ResourceNotFoundException;
import com.skillsync.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.skillsync.notification.service.impl.NotificationServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void createNotificationBuildsAndSavesEntity() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Notification saved = notificationService.createNotification(2L, "SESSION_BOOKED", "Booked");

        verify(notificationRepository).save(saved);
    }

    @Test
    void markAsReadRejectsUnknownNotification() {
        when(notificationRepository.findById(7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.markAsRead(7L));
    }
}
