package com.skillsync.session.repository;

import com.skillsync.session.entity.MentoringSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<MentoringSession, Long> {
    List<MentoringSession> findByMentorId(Long mentorId);
    List<MentoringSession> findByMenteeId(Long menteeId);
}
