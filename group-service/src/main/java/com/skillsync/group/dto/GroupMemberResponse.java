package com.skillsync.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.group.entity.GroupMember;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupMemberResponse {

    private final Long id;
    private final Long groupId;
    private final Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime joinedAt;

    public GroupMemberResponse(GroupMember m) {
        this.id = m.getId();
        this.groupId = m.getGroupId();
        this.userId = m.getUserId();
        this.joinedAt = m.getJoinedAt();
    }
}
