package com.skillsync.group.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GroupMemberRequest {

    @NotNull(message = "User ID is required")
    private Long userId;
}
