package com.skillsync.skill.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.skill.entity.Skill;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SkillResponse {

    private final Long id;
    private final String name;
    private final String category;
    private final String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public SkillResponse(Skill s) {
        this.id = s.getId();
        this.name = s.getName();
        this.category = s.getCategory();
        this.description = s.getDescription();
        this.createdAt = s.getCreatedAt();
    }
}
