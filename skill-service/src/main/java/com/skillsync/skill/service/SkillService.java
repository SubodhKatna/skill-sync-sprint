package com.skillsync.skill.service;

import com.skillsync.skill.dto.SkillResponse;
import com.skillsync.skill.entity.Skill;

import java.util.List;

public interface SkillService {
    List<SkillResponse> getAllSkills();
    SkillResponse getById(Long id);
    SkillResponse createSkill(Skill skill);
}
