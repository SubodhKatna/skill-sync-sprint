package com.skillsync.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillsync.group.dto.CreateGroupRequest;
import com.skillsync.group.dto.GroupMemberRequest;
import com.skillsync.group.dto.GroupMemberResponse;
import com.skillsync.group.dto.GroupResponse;
import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.security.AuthEntryPoint;
import com.skillsync.group.security.CustomAccessDeniedHandler;
import com.skillsync.group.security.SecurityConfig;
import com.skillsync.group.service.GroupService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "jwt.secret=01234567890123456789012345678901")
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GroupService groupService;

    @MockBean
    private AuthEntryPoint authEntryPoint;

    @MockBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Test
    @WithMockUser
    void createGroupReturnsSavedGroup() throws Exception {
        CreateGroupRequest request = new CreateGroupRequest();
        request.setName("Backend Circle");
        request.setCreatedBy(9L);

        LearningGroup entity = new LearningGroup();
        entity.setId(1L);
        entity.setName("Backend Circle");
        entity.setCreatedBy(9L);
        entity.setMaxMembers(10);

        when(groupService.createGroup(any(CreateGroupRequest.class))).thenReturn(new GroupResponse(entity));

        mockMvc.perform(post("/groups")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Backend Circle"));
    }

    @Test
    @WithMockUser
    void getMembersReturnsList() throws Exception {
        GroupMember member = new GroupMember();
        member.setId(1L);
        member.setGroupId(4L);
        member.setUserId(7L);

        when(groupService.getGroupMembers(4L)).thenReturn(List.of(new GroupMemberResponse(member)));

        mockMvc.perform(get("/groups/4/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(7));
    }

    @Test
    @WithMockUser
    void leaveGroupReturnsOk() throws Exception {
        GroupMemberRequest request = new GroupMemberRequest();
        request.setUserId(11L);

        doNothing().when(groupService).leaveGroup(eq(5L), eq(11L));

        mockMvc.perform(post("/groups/5/leave")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
