package com.skillsync.user.service;

import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;

import java.util.List;

public interface UserService {
    UserProfile createProfile(UserProfile profile);
    UserProfile getProfileByUserId(Long userId);
    UserProfile getProfileById(Long id);
    UserProfile updateProfile(Long id, UserProfile updated);
    List<UserProfile> getAllProfiles();
    UserSkill addSkill(Long userId, UserSkill skill);
    List<UserSkill> getUserSkills(Long userId);
}
