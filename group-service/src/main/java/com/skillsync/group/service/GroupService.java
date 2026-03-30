package com.skillsync.group.service;

import com.skillsync.group.dto.CreateGroupRequest;
import com.skillsync.group.dto.GroupMemberResponse;
import com.skillsync.group.dto.GroupResponse;

import java.util.List;

public interface GroupService {
    GroupResponse createGroup(CreateGroupRequest request);
    void joinGroup(Long groupId, Long userId);
    void leaveGroup(Long groupId, Long userId);
    List<GroupMemberResponse> getGroupMembers(Long groupId);
    List<GroupResponse> getGroupsBySkillId(Long skillId);
    List<GroupResponse> getAllGroups();
    GroupResponse getGroupById(Long id);
}
