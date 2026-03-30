package com.skillsync.group.service.impl;

import com.skillsync.group.client.SkillServiceClient;
import com.skillsync.group.client.UserServiceClient;
import com.skillsync.group.dto.CreateGroupRequest;
import com.skillsync.group.dto.GroupMemberResponse;
import com.skillsync.group.dto.GroupResponse;
import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.exception.BadRequestException;
import com.skillsync.group.exception.ConflictException;
import com.skillsync.group.exception.ResourceNotFoundException;
import com.skillsync.group.repository.GroupMemberRepository;
import com.skillsync.group.repository.LearningGroupRepository;
import com.skillsync.group.service.GroupService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final LearningGroupRepository groupRepository;
    private final GroupMemberRepository memberRepository;
    private final UserServiceClient userServiceClient;
    private final SkillServiceClient skillServiceClient;

    @Override
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        try {
            userServiceClient.getUserByUserId(request.getCreatedBy());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + request.getCreatedBy());
        }

        if (request.getSkillId() != null) {
            try {
                skillServiceClient.getSkillById(request.getSkillId());
            } catch (FeignException.NotFound e) {
                throw new ResourceNotFoundException("Skill not found with id: " + request.getSkillId());
            }
        }

        LearningGroup group = new LearningGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setSkillId(request.getSkillId());
        group.setCreatedBy(request.getCreatedBy());
        group.setMaxMembers(request.getMaxMembers() != null && request.getMaxMembers() >= 2
                ? request.getMaxMembers() : 10);

        LearningGroup saved = groupRepository.save(group);

        GroupMember creator = new GroupMember();
        creator.setGroupId(saved.getId());
        creator.setUserId(saved.getCreatedBy());
        memberRepository.save(creator);

        return new GroupResponse(saved);
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        LearningGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ConflictException("User " + userId + " is already a member of group " + groupId);
        }

        if (memberRepository.countByGroupId(groupId) >= group.getMaxMembers()) {
            throw new BadRequestException("Group is full (max " + group.getMaxMembers() + " members)");
        }

        try {
            userServiceClient.getUserByUserId(userId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        GroupMember member = new GroupMember();
        member.setGroupId(groupId);
        member.setUserId(userId);
        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
        GroupMember member = memberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " is not a member of group " + groupId));
        memberRepository.delete(member);
    }

    @Override
    public List<GroupMemberResponse> getGroupMembers(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
        return memberRepository.findByGroupId(groupId).stream().map(GroupMemberResponse::new).toList();
    }

    @Override
    public List<GroupResponse> getGroupsBySkillId(Long skillId) {
        return groupRepository.findBySkillId(skillId).stream().map(GroupResponse::new).toList();
    }

    @Override
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream().map(GroupResponse::new).toList();
    }

    @Override
    public GroupResponse getGroupById(Long id) {
        return new GroupResponse(groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id)));
    }
}
