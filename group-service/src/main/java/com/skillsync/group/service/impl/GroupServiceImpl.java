package com.skillsync.group.service.impl;

import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.exception.BadRequestException;
import com.skillsync.group.exception.ConflictException;
import com.skillsync.group.exception.ResourceNotFoundException;
import com.skillsync.group.repository.GroupMemberRepository;
import com.skillsync.group.repository.LearningGroupRepository;
import com.skillsync.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private LearningGroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository memberRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;

    @Value("${skill.service.url}")
    private String skillServiceUrl;

    @Override
    @Transactional
    public LearningGroup createGroup(LearningGroup group) {
        // Validate creator exists in user-service
        try {
            restTemplate.getForObject(userServiceUrl + "/users/" + group.getCreatedBy(), java.util.Map.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found with id: " + group.getCreatedBy());
        }

        // Validate skill exists in skill-service (if provided)
        if (group.getSkillId() != null) {
            try {
                restTemplate.getForObject(skillServiceUrl + "/skills/" + group.getSkillId(), java.util.Map.class);
            } catch (Exception e) {
                throw new ResourceNotFoundException("Skill not found with id: " + group.getSkillId());
            }
        }

        if (group.getMaxMembers() == null || group.getMaxMembers() < 2) {
            group.setMaxMembers(10);
        }

        LearningGroup saved = groupRepository.save(group);

        // Auto-add creator as first member
        GroupMember creator = new GroupMember();
        creator.setGroupId(saved.getId());
        creator.setUserId(saved.getCreatedBy());
        memberRepository.save(creator);

        return saved;
    }

    @Override
    @Transactional
    public void joinGroup(Long groupId, Long userId) {
        LearningGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        if (memberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new ConflictException("User " + userId + " is already a member of group " + groupId);
        }

        long currentCount = memberRepository.countByGroupId(groupId);
        if (currentCount >= group.getMaxMembers()) {
            throw new BadRequestException("Group is full (max " + group.getMaxMembers() + " members)");
        }

        // Validate user exists
        try {
            restTemplate.getForObject(userServiceUrl + "/users/" + userId, java.util.Map.class);
        } catch (Exception e) {
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
    public List<GroupMember> getGroupMembers(Long groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
        return memberRepository.findByGroupId(groupId);
    }

    @Override
    public List<LearningGroup> getGroupsBySkillId(Long skillId) {
        return groupRepository.findBySkillId(skillId);
    }

    @Override
    public List<LearningGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public LearningGroup getGroupById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
    }
}
