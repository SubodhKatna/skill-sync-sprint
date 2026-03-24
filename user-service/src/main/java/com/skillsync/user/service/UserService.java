package com.skillsync.user.service;

import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.exception.ConflictException;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.repository.UserSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserProfileRepository profileRepository;

    @Autowired
    private UserSkillRepository skillRepository;

    public UserProfile createProfile(UserProfile profile) {
        if (profileRepository.existsByUserId(profile.getUserId())) {
            throw new ConflictException("Profile already exists for user: " + profile.getUserId());
        }
        return profileRepository.save(profile);
    }

    public UserProfile getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId));
    }

    public UserProfile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
    }

    public UserProfile updateProfile(Long id, UserProfile updated) {
        UserProfile existing = getProfileById(id);
        existing.setName(updated.getName());
        existing.setBio(updated.getBio());
        existing.setPhone(updated.getPhone());
        existing.setProfileImageUrl(updated.getProfileImageUrl());
        if (updated.getEmail() != null) {
            existing.setEmail(updated.getEmail());
        }
        return profileRepository.save(existing);
    }

    public List<UserProfile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public UserSkill addSkill(Long userId, UserSkill skill) {
        skill.setUserId(userId);
        return skillRepository.save(skill);
    }

    public List<UserSkill> getUserSkills(Long userId) {
        return skillRepository.findByUserId(userId);
    }
}
