package com.skillsync.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.user.dto.UserProfileResponse;
import com.skillsync.user.dto.UserSkillResponse;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.security.AuthEntryPoint;
import com.skillsync.user.security.CustomAccessDeniedHandler;
import com.skillsync.user.security.SecurityConfig;
import com.skillsync.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=01234567890123456789012345678901")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthEntryPoint authEntryPoint;

    @MockBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @WithMockUser
    void createProfileReturnsSavedProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserId(10L);
        profile.setName("Asha");
        profile.setEmail("asha@example.com");

        when(userService.createProfile(any(UserProfile.class))).thenReturn(new UserProfileResponse(profile));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Asha"));
    }

    @Test
    @WithMockUser
    void getProfileReturnsProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserId(10L);
        profile.setName("Asha");

        when(userService.getProfileById(1L)).thenReturn(new UserProfileResponse(profile));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Asha"));
    }

    @Test
    @WithMockUser
    void getProfileReturnsNotFound() throws Exception {
        when(userService.getProfileById(99L)).thenThrow(new ResourceNotFoundException("Profile not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getUserSkillsReturnsSkillList() throws Exception {
        UserSkill skill = new UserSkill();
        skill.setId(5L);
        skill.setUserId(10L);
        skill.setSkillName("Java");

        when(userService.getUserSkills(10L)).thenReturn(List.of(new UserSkillResponse(skill)));

        mockMvc.perform(get("/users/10/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skillName").value("Java"));
    }
}
