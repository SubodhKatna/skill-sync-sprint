package com.skillsync.group.repository;

import com.skillsync.group.entity.LearningGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LearningGroupRepository extends JpaRepository<LearningGroup, Long> {
    List<LearningGroup> findBySkillId(Long skillId);
}
