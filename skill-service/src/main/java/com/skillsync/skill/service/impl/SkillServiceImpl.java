package com.skillsync.skill.service.impl;

import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.exception.ConflictException;
import com.skillsync.skill.exception.ResourceNotFoundException;
import com.skillsync.skill.repository.SkillRepository;
import com.skillsync.skill.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillServiceImpl implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    @Override
    public Skill getById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    }

    @Override
    public Skill createSkill(Skill skill) {
        if (skillRepository.existsByName(skill.getName())) {
            throw new ConflictException("Skill already exists with name: " + skill.getName());
        }
        return skillRepository.save(skill);
    }
}
