package com.skillsync.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.service.UserService;
import com.skillsync.user.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void createProfileReturnsSavedProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserId(10L);
        profile.setName("Asha");
        profile.setEmail("asha@example.com");

        when(userService.createProfile(any(UserProfile.class))).thenReturn(profile);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profile)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Asha"));
    }

    @Test
    void getProfileReturnsProfile() throws Exception {
        UserProfile profile = new UserProfile();
        profile.setId(1L);
        profile.setUserId(10L);
        profile.setName("Asha");

        when(userService.getProfileById(1L)).thenReturn(profile);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Asha"));
    }

    @Test
    void getProfileReturnsNotFound() throws Exception {
        when(userService.getProfileById(99L)).thenThrow(new ResourceNotFoundException("Profile not found"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserSkillsReturnsSkillList() throws Exception {
        UserSkill skill = new UserSkill();
        skill.setId(5L);
        skill.setUserId(10L);
        skill.setSkillName("Java");

        when(userService.getUserSkills(10L)).thenReturn(List.of(skill));

        mockMvc.perform(get("/users/10/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].skillName").value("Java"));
    }
}
