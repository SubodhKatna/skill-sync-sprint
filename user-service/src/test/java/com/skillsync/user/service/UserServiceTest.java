package com.skillsync.user.service;

import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.exception.ConflictException;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.repository.UserSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserProfileRepository profileRepository;

    @Mock
    private UserSkillRepository skillRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createProfileRejectsDuplicateUserId() {
        UserProfile profile = new UserProfile();
        profile.setUserId(10L);

        when(profileRepository.existsByUserId(10L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createProfile(profile));
    }

    @Test
    void addSkillAssignsUserId() {
        UserSkill skill = new UserSkill();
        skill.setSkillName("Java");
        when(skillRepository.save(any(UserSkill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSkill saved = userService.addSkill(12L, skill);

        assertEquals(12L, saved.getUserId());
        assertEquals("Java", saved.getSkillName());
    }
}
