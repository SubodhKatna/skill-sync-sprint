package com.skillsync.group.service;

import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.exception.BadRequestException;
import com.skillsync.group.repository.GroupMemberRepository;
import com.skillsync.group.repository.LearningGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.skillsync.group.service.impl.GroupServiceImpl;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    private LearningGroupRepository groupRepository;

    @Mock
    private GroupMemberRepository memberRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    void joinGroupRejectsFullGroup() {
        LearningGroup group = new LearningGroup();
        group.setId(1L);
        group.setMaxMembers(2);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(memberRepository.existsByGroupIdAndUserId(1L, 9L)).thenReturn(false);
        when(memberRepository.countByGroupId(1L)).thenReturn(2L);

        assertThrows(BadRequestException.class, () -> groupService.joinGroup(1L, 9L));
    }
}
