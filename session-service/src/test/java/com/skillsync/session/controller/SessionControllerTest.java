package com.skillsync.session.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.security.JwtAuthFilter;
import com.skillsync.session.security.SecurityConfig;
import com.skillsync.session.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
@Import(SecurityConfig.class)
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser
    void createSessionReturnsSavedSession() throws Exception {
        SessionRequest request = new SessionRequest();
        request.setMentorId(2L);
        request.setMenteeId(3L);
        request.setSkillId(1L);
        request.setStartTime(LocalDateTime.of(2027, 3, 24, 10, 0));
        request.setEndTime(LocalDateTime.of(2027, 3, 24, 11, 0));

        SessionResponse response = SessionResponse.builder()
                .id(1L)
                .mentorId(2L)
                .menteeId(3L)
                .skillId(1L)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status("SCHEDULED")
                .build();

        when(sessionService.createSession(any(SessionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/sessions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"));
    }

    @Test
    @WithMockUser
    void getSessionByIdReturnsResponse() throws Exception {
        SessionResponse response = SessionResponse.builder()
                .id(8L)
                .mentorId(1L)
                .menteeId(2L)
                .skillId(1L)
                .status("SCHEDULED")
                .build();

        when(sessionService.getSessionById(8L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/sessions/8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(8));
    }
}
