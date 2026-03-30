package com.skillsync.user.controller;

import com.skillsync.user.dto.*;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserProfileResponse> createProfile(@Valid @RequestBody UserProfile profile) {
        return new ResponseEntity<>(userService.createProfile(profile), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfileById(id));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<UserProfileResponse> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfileByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponse> updateProfile(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateProfileRequest request) {
        UserProfile profile = new UserProfile();
        profile.setName(request.getName());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());
        profile.setProfileImageUrl(request.getProfileImageUrl());
        profile.setEmail(request.getEmail());
        return ResponseEntity.ok(userService.updateProfile(id, profile));
    }

    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles() {
        return ResponseEntity.ok(userService.getAllProfiles());
    }

    @PostMapping("/{userId}/skills")
    public ResponseEntity<UserSkillResponse> addSkill(@PathVariable Long userId,
                                                       @Valid @RequestBody AddSkillRequest request) {
        UserSkill skill = new UserSkill();
        skill.setSkillName(request.getSkillName());
        skill.setProficiencyLevel(request.getProficiencyLevel());
        return new ResponseEntity<>(userService.addSkill(userId, skill), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/skills")
    public ResponseEntity<List<UserSkillResponse>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserSkills(userId));
    }

    @PutMapping("/{userId}/skills/{skillId}")
    public ResponseEntity<UserSkillResponse> updateSkillLevel(@PathVariable Long userId,
                                                               @PathVariable Long skillId,
                                                               @Valid @RequestBody UpdateSkillLevelRequest request) {
        return ResponseEntity.ok(userService.updateSkillLevel(userId, skillId, request.getProficiencyLevel()));
    }
}
