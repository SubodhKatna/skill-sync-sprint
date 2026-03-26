package com.skillsync.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupService groupService;

    @Test
    void createGroupReturnsSavedGroup() throws Exception {
        LearningGroup group = new LearningGroup();
        group.setId(1L);
        group.setName("Backend Circle");
        group.setCreatedBy(9L);

        when(groupService.createGroup(any(LearningGroup.class))).thenReturn(group);

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Backend Circle"));
    }

    @Test
    void getMembersReturnsList() throws Exception {
        GroupMember member = new GroupMember();
        member.setUserId(7L);
        when(groupService.getGroupMembers(4L)).thenReturn(List.of(member));

        mockMvc.perform(get("/groups/4/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(7));
    }

    @Test
    void leaveGroupReturnsOk() throws Exception {
        doNothing().when(groupService).leaveGroup(eq(5L), eq(11L));

        mockMvc.perform(post("/groups/5/leave")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("userId", 11L))))
                .andExpect(status().isOk());
    }
}
