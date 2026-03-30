package com.skillsync.mentor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.security.JwtAuthFilter;
import com.skillsync.mentor.security.SecurityConfig;
import com.skillsync.mentor.service.MentorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorController.class)
@Import(SecurityConfig.class)
class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MentorService mentorService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser
    void applyAsMentorReturnsMentor() throws Exception {
        Mentor mentor = new Mentor();
        mentor.setId(2L);
        mentor.setName("Priya");

        MentorApplicationRequest request = new MentorApplicationRequest();
        request.setUserId(12L);
        request.setName("Priya");
        request.setHourlyRate(500.0);
        request.setExperienceYears(4);
        request.setSkillIds(List.of(1L, 2L));

        when(mentorService.applyAsMentor(any(MentorApplicationRequest.class))).thenReturn(mentor);

        mockMvc.perform(post("/mentors/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Priya"));
    }

    @Test
    @WithMockUser
    void updateRatingRejectsMissingValues() throws Exception {
        doNothing().when(mentorService).updateRating(any(), any(), any());

        mockMvc.perform(put("/mentors/2/rating")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("rating", 4.5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser
    void getAllMentorsReturnsList() throws Exception {
        Mentor mentor = new Mentor();
        mentor.setId(2L);
        mentor.setName("Priya");
        when(mentorService.getAllMentors()).thenReturn(List.of(mentor));

        mockMvc.perform(get("/mentors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2));
    }

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void approveMentorReturnsApproved() throws Exception {
        Mentor mentor = new Mentor();
        mentor.setId(2L);
        mentor.setStatus(Mentor.MentorStatus.APPROVED);
        when(mentorService.approveMentor(2L)).thenReturn(mentor);

        mockMvc.perform(put("/mentors/2/approve").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
