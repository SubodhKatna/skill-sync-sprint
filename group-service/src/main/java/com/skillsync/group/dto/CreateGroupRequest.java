package com.skillsync.group.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    private Long skillId;

    @NotNull(message = "Creator user ID is required")
    private Long createdBy;

    @Min(value = 2, message = "Max members must be at least 2")
    private Integer maxMembers;
}
