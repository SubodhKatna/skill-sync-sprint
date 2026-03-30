package com.skillsync.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateSkillLevelRequest {

    @NotBlank(message = "Proficiency level is required")
    private String proficiencyLevel; // BEGINNER, INTERMEDIATE, ADVANCED
}
