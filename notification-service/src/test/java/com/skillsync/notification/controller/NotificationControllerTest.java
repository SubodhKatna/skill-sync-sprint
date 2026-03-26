package com.skillsync.notification.controller;

import com.skillsync.notification.entity.Notification;
import com.skillsync.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    void getUnreadNotificationsReturnsList() throws Exception {
        Notification notification = new Notification();
        notification.setId(5L);
        notification.setUserId(2L);
        notification.setMessage("Session booked");

        when(notificationService.getUnreadNotifications(2L)).thenReturn(List.of(notification));

        mockMvc.perform(get("/notifications/user/2/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Session booked"));
    }

    @Test
    void markAsReadReturnsNotification() throws Exception {
        Notification notification = new Notification();
        notification.setId(5L);
        notification.setRead(true);

        when(notificationService.markAsRead(5L)).thenReturn(notification);

        mockMvc.perform(put("/notifications/5/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }
}
