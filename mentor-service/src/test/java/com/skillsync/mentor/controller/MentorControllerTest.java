package com.skillsync.mentor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.dto.MentorResponse;
import com.skillsync.mentor.dto.UpdateRatingRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.security.AuthEntryPoint;
import com.skillsync.mentor.security.CustomAccessDeniedHandler;
import com.skillsync.mentor.security.SecurityConfig;
import com.skillsync.mentor.service.MentorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
@TestPropertySource(properties = "jwt.secret=01234567890123456789012345678901")
class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MentorService mentorService;

    @MockBean
    private AuthEntryPoint authEntryPoint;

    @MockBean
    private CustomAccessDeniedHandler accessDeniedHandler;

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

        when(mentorService.applyAsMentor(any(MentorApplicationRequest.class)))
                .thenReturn(new MentorResponse(mentor));

        mockMvc.perform(post("/mentors/apply")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Priya"));
    }

    @Test
    @WithMockUser
    void updateRatingRejectsMissingValues() throws Exception {
        UpdateRatingRequest request = new UpdateRatingRequest();
        request.setRating(4.5);
        // totalReviews intentionally omitted to trigger validation error

        mockMvc.perform(put("/mentors/2/rating")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":4.5}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void getAllMentorsReturnsList() throws Exception {
        Mentor mentor = new Mentor();
        mentor.setId(2L);
        mentor.setName("Priya");

        when(mentorService.getAllMentors()).thenReturn(List.of(new MentorResponse(mentor)));

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

        when(mentorService.approveMentor(2L)).thenReturn(new MentorResponse(mentor));

        mockMvc.perform(put("/mentors/2/approve").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }
}
