package com.skillsync.skill.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.security.JwtAuthFilter;
import com.skillsync.skill.security.SecurityConfig;
import com.skillsync.skill.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
@Import(SecurityConfig.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillService skillService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    @WithMockUser(authorities = "ROLE_ADMIN")
    void createSkillReturnsSavedSkill() throws Exception {
        Skill skill = new Skill(1L, "Java", "Programming", "Core Java");
        when(skillService.createSkill(any(Skill.class))).thenReturn(skill);

        mockMvc.perform(post("/skills")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    void getAllSkillsReturnsList() throws Exception {
        when(skillService.getAllSkills()).thenReturn(List.of(new Skill(1L, "Java", "Programming", "Core Java")));

        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Programming"));
    }

    @Test
    @WithMockUser
    void createSkillForbiddenForNonAdmin() throws Exception {
        Skill skill = new Skill(1L, "Java", "Programming", "Core Java");

        mockMvc.perform(post("/skills")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(skill)))
                .andExpect(status().isForbidden());
    }
}
