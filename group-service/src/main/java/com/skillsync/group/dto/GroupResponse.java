package com.skillsync.group.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.group.entity.LearningGroup;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GroupResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Long skillId;
    private final Long createdBy;
    private final Integer maxMembers;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public GroupResponse(LearningGroup g) {
        this.id = g.getId();
        this.name = g.getName();
        this.description = g.getDescription();
        this.skillId = g.getSkillId();
        this.createdBy = g.getCreatedBy();
        this.maxMembers = g.getMaxMembers();
        this.createdAt = g.getCreatedAt();
    }
}
