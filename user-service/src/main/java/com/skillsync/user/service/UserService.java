package com.skillsync.user.service;

import com.skillsync.user.dto.UserProfileResponse;
import com.skillsync.user.dto.UserSkillResponse;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;

import java.util.List;

public interface UserService {
    UserProfileResponse createProfile(UserProfile profile);
    UserProfileResponse getProfileByUserId(Long userId);
    UserProfileResponse getProfileById(Long id);
    UserProfileResponse updateProfile(Long id, UserProfile updated);
    List<UserProfileResponse> getAllProfiles();
    UserSkillResponse addSkill(Long userId, UserSkill skill);
    List<UserSkillResponse> getUserSkills(Long userId);
    UserSkillResponse updateSkillLevel(Long userId, Long skillId, String proficiencyLevel);
}
