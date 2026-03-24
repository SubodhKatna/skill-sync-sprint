package com.skillsync.skill.service;

import com.skillsync.skill.entity.Skill;
import com.skillsync.skill.exception.ConflictException;
import com.skillsync.skill.repository.SkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @InjectMocks
    private SkillService skillService;

    @Test
    void createSkillRejectsDuplicates() {
        Skill skill = new Skill(null, "Java", null, null);
        when(skillRepository.existsByName("Java")).thenReturn(true);

        assertThrows(ConflictException.class, () -> skillService.createSkill(skill));
    }

    @Test
    void createSkillSavesNewSkill() {
        Skill skill = new Skill(null, "Java", "Programming", null);
        when(skillRepository.existsByName("Java")).thenReturn(false);
        when(skillRepository.save(any(Skill.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Skill saved = skillService.createSkill(skill);

        assertEquals("Java", saved.getName());
    }
}
