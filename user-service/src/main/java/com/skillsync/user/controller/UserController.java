package com.skillsync.user.controller;

import com.skillsync.user.dto.UpdateSkillLevelRequest;
import com.skillsync.user.entity.UserProfile;
import com.skillsync.user.entity.UserSkill;
import com.skillsync.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserProfile> createProfile(@RequestBody UserProfile profile) {
        return ResponseEntity.ok(userService.createProfile(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfileById(id));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<UserProfile> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfileByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateProfile(@PathVariable Long id, @RequestBody UserProfile profile) {
        return ResponseEntity.ok(userService.updateProfile(id, profile));
    }

    @GetMapping
    public ResponseEntity<List<UserProfile>> getAllProfiles() {
        return ResponseEntity.ok(userService.getAllProfiles());
    }

    @PostMapping("/{userId}/skills")
    public ResponseEntity<UserSkill> addSkill(@PathVariable Long userId, @RequestBody UserSkill skill) {
        return ResponseEntity.ok(userService.addSkill(userId, skill));
    }

    @GetMapping("/{userId}/skills")
    public ResponseEntity<List<UserSkill>> getUserSkills(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserSkills(userId));
    }

    @PutMapping("/{userId}/skills/{skillId}")
    public ResponseEntity<UserSkill> updateSkillLevel(@PathVariable Long userId,
                                                       @PathVariable Long skillId,
                                                       @Valid @RequestBody UpdateSkillLevelRequest request) {
        return ResponseEntity.ok(userService.updateSkillLevel(userId, skillId, request.getProficiencyLevel()));
    }
}
