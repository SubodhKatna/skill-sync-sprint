package com.skillsync.skill.service.impl;

import com.skillsync.skill.dto.SkillResponse;
import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.exception.ConflictException;
import com.skillsync.skill.exception.ResourceNotFoundException;
import com.skillsync.skill.repository.SkillRepository;
import com.skillsync.skill.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;

    @Override
    public List<SkillResponse> getAllSkills() {
        return skillRepository.findAll().stream().map(SkillResponse::new).toList();
    }

    @Override
    public SkillResponse getById(Long id) {
        return new SkillResponse(skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id)));
    }

    @Override
    public SkillResponse createSkill(Skill skill) {
        if (skillRepository.existsByName(skill.getName())) {
            throw new ConflictException("Skill already exists with name: " + skill.getName());
        }
        return new SkillResponse(skillRepository.save(skill));
    }
}
