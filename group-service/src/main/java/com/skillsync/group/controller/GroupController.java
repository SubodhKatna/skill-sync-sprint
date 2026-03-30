package com.skillsync.group.controller;

import com.skillsync.group.dto.CreateGroupRequest;
import com.skillsync.group.dto.GroupMemberRequest;
import com.skillsync.group.dto.GroupMemberResponse;
import com.skillsync.group.dto.GroupResponse;
import com.skillsync.group.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return new ResponseEntity<>(groupService.createGroup(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        return ResponseEntity.ok(groupService.getAllGroups());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroupById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupById(id));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinGroup(@PathVariable Long id,
                                           @Valid @RequestBody GroupMemberRequest request) {
        groupService.joinGroup(id, request.getUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveGroup(@PathVariable Long id,
                                            @Valid @RequestBody GroupMemberRequest request) {
        groupService.leaveGroup(id, request.getUserId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<GroupMemberResponse>> getGroupMembers(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getGroupMembers(id));
    }

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<GroupResponse>> getGroupsBySkillId(@PathVariable Long skillId) {
        return ResponseEntity.ok(groupService.getGroupsBySkillId(skillId));
    }
}
