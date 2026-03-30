package com.skillsync.user.dto;

import com.skillsync.user.entity.UserSkill;
import lombok.Getter;

@Getter
public class UserSkillResponse {

    private final Long id;
    private final Long userId;
    private final String skillName;
    private final String proficiencyLevel;

    public UserSkillResponse(UserSkill s) {
        this.id = s.getId();
        this.userId = s.getUserId();
        this.skillName = s.getSkillName();
        this.proficiencyLevel = s.getProficiencyLevel();
    }
}
