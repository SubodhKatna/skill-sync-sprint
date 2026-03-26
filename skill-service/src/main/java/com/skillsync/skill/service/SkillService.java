package com.skillsync.skill.service;

import com.skillsync.skill.entity.Skill;

import java.util.List;

public interface SkillService {
    List<Skill> getAllSkills();
    Skill getById(Long id);
    Skill createSkill(Skill skill);
}
