package com.skillsync.user.service.impl;

import com.skillsync.user.dto.UserProfileResponse;
import com.skillsync.user.dto.UserSkillResponse;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.exception.BadRequestException;
import com.skillsync.user.exception.ConflictException;
import com.skillsync.user.exception.ResourceNotFoundException;
import com.skillsync.user.repository.UserProfileRepository;
import com.skillsync.user.repository.UserSkillRepository;
import com.skillsync.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository profileRepository;
    private final UserSkillRepository skillRepository;

    private static final Set<String> VALID_LEVELS = Set.of("BEGINNER", "INTERMEDIATE", "ADVANCED");

    @Override
    public UserProfileResponse createProfile(UserProfile profile) {
        if (profileRepository.existsByUserId(profile.getUserId())) {
            throw new ConflictException("Profile already exists for user: " + profile.getUserId());
        }
        return new UserProfileResponse(profileRepository.save(profile));
    }

    @Override
    public UserProfileResponse getProfileByUserId(Long userId) {
        return new UserProfileResponse(profileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + userId)));
    }

    @Override
    public UserProfileResponse getProfileById(Long id) {
        return new UserProfileResponse(profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id)));
    }

    @Override
    public UserProfileResponse updateProfile(Long id, UserProfile updated) {
        UserProfile existing = profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        existing.setName(updated.getName());
        existing.setBio(updated.getBio());
        existing.setPhone(updated.getPhone());
        existing.setProfileImageUrl(updated.getProfileImageUrl());
        if (updated.getEmail() != null) {
            existing.setEmail(updated.getEmail());
        }
        return new UserProfileResponse(profileRepository.save(existing));
    }

    @Override
    public List<UserProfileResponse> getAllProfiles() {
        return profileRepository.findAll().stream().map(UserProfileResponse::new).toList();
    }

    @Override
    public UserSkillResponse addSkill(Long userId, UserSkill skill) {
        if (skillRepository.existsByUserIdAndSkillName(userId, skill.getSkillName())) {
            throw new ConflictException("Skill already exists for user: " + skill.getSkillName());
        }
        skill.setUserId(userId);
        return new UserSkillResponse(skillRepository.save(skill));
    }

    @Override
    public List<UserSkillResponse> getUserSkills(Long userId) {
        return skillRepository.findByUserId(userId).stream().map(UserSkillResponse::new).toList();
    }

    @Override
    public UserSkillResponse updateSkillLevel(Long userId, Long skillId, String proficiencyLevel) {
        String level = proficiencyLevel.trim().toUpperCase();
        if (!VALID_LEVELS.contains(level)) {
            throw new BadRequestException("Invalid proficiency level: '" + proficiencyLevel + "'. Allowed: BEGINNER, INTERMEDIATE, ADVANCED");
        }
        UserSkill skill = skillRepository.findByIdAndUserId(skillId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + skillId + " for user: " + userId));
        skill.setProficiencyLevel(level);
        return new UserSkillResponse(skillRepository.save(skill));
    }
}
