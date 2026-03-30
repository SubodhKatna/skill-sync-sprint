package com.skillsync.notification.controller;

import com.skillsync.notification.entity.Notification;
import com.skillsync.notification.security.JwtAuthFilter;
import com.skillsync.notification.security.SecurityConfig;
import com.skillsync.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(SecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser
    void getUnreadNotificationsReturnsList() throws Exception {
        Notification notification = new Notification();
        notification.setId(5L);
        notification.setUserId(2L);
        notification.setMessage("Session booked");
        notification.setType("SESSION_SCHEDULED");

        when(notificationService.getUnreadNotifications(2L)).thenReturn(List.of(notification));

        mockMvc.perform(get("/notifications/user/2/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("Session booked"));
    }

    @Test
    @WithMockUser
    void markAsReadReturnsNotification() throws Exception {
        Notification notification = new Notification();
        notification.setId(5L);
        notification.setIsRead(true);

        when(notificationService.markAsRead(5L)).thenReturn(notification);

        mockMvc.perform(put("/notifications/5/read").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isRead").value(true));
    }
}
