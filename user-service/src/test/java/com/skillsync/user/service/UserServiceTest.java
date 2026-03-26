package com.skillsync.user.service;

import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.exception.ConflictException;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.repository.UserSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.skillsync.user.service.impl.UserServiceImpl;
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
    private UserServiceImpl userService;

    @Test
    void createProfileRejectsDuplicateUserId() {
        UserProfile profile = new UserProfile();
        profile.setUserId(10L);

        when(profileRepository.existsByUserId(10L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createProfile(profile));
    }

    @Test
    void createProfileSuccess() {
        UserProfile profile = new UserProfile();
        profile.setUserId(10L);
        profile.setEmail("test@test.com");

        when(profileRepository.existsByUserId(10L)).thenReturn(false);
        when(profileRepository.save(any(UserProfile.class))).thenReturn(profile);

        UserProfile saved = userService.createProfile(profile);
        assertEquals(10L, saved.getUserId());
    }

    @Test
    void getProfileByUserIdSuccess() {
        UserProfile profile = new UserProfile();
        profile.setUserId(10L);
        when(profileRepository.findByUserId(10L)).thenReturn(java.util.Optional.of(profile));

        UserProfile found = userService.getProfileByUserId(10L);
        assertEquals(10L, found.getUserId());
    }

    @Test
    void getProfileByUserIdNotFound() {
        when(profileRepository.findByUserId(10L)).thenReturn(java.util.Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getProfileByUserId(10L));
    }

    @Test
    void updateProfileSuccess() {
        UserProfile existing = new UserProfile();
        existing.setId(1L);
        existing.setName("Old Name");

        UserProfile update = new UserProfile();
        update.setName("New Name");

        when(profileRepository.findById(1L)).thenReturn(java.util.Optional.of(existing));
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile updated = userService.updateProfile(1L, update);
        assertEquals("New Name", updated.getName());
    }

    @Test
    void getAllProfilesReturnsList() {
        when(profileRepository.findAll()).thenReturn(List.of(new UserProfile(), new UserProfile()));
        List<UserProfile> profiles = userService.getAllProfiles();
        assertEquals(2, profiles.size());
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
