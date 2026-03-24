package com.skillsync.skill.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSkillRequest {

    @NotBlank(message = "Skill name is required")
    private String name;

    private String description;

    private String category;
}
