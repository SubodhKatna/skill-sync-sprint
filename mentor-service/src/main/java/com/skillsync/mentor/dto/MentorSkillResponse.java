package com.skillsync.mentor.dto;

import com.skillsync.mentor.entity.MentorSkill;
import lombok.Getter;

@Getter
public class MentorSkillResponse {

    private final Long id;
    private final Long mentorId;
    private final Long skillId;
    private final String skillName;

    public MentorSkillResponse(MentorSkill s) {
        this.id = s.getId();
        this.mentorId = s.getMentorId();
        this.skillId = s.getSkillId();
        this.skillName = s.getSkillName();
    }
}
