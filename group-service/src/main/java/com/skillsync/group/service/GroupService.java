package com.skillsync.group.service;

import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;

import java.util.List;

public interface GroupService {
    LearningGroup createGroup(LearningGroup group);
    void joinGroup(Long groupId, Long userId);
    void leaveGroup(Long groupId, Long userId);
    List<GroupMember> getGroupMembers(Long groupId);
    List<LearningGroup> getGroupsBySkillId(Long skillId);
    List<LearningGroup> getAllGroups();
    LearningGroup getGroupById(Long id);
}
