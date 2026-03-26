package com.skillsync.group.controller;

import com.skillsync.group.entity.GroupMember;
import com.skillsync.group.entity.LearningGroup;
import com.skillsync.group.exception.BadRequestException;
import com.skillsync.group.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PostMapping
    public ResponseEntity<LearningGroup> createGroup(@RequestBody LearningGroup group) {
        return ResponseEntity.ok(groupService.createGroup(group));
    }

    @GetMapping
    public ResponseEntity<List<LearningGroup>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LearningGroup> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinGroup(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = extractUserId(body);
        groupService.joinGroup(id, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Long userId = extractUserId(body);
        groupService.leaveGroup(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMember>> getGroupMembers(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupMembers(id));
    }

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<LearningGroup>> getGroupsBySkillId(@PathVariable Long skillId) {
        return ResponseEntity.ok(groupService.getGroupsBySkillId(skillId));
    }

    private Long extractUserId(Map<String, Object> body) {
        if (body == null || body.get("userId") == null) {
            throw new BadRequestException("userId is required");
        }
        return ((Number) body.get("userId")).longValue();
    }
}
