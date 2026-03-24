package com.skillsync.mentor.repository;

import com.skillsync.mentor.entity.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MentorRepository extends JpaRepository<Mentor, Long> {
    boolean existsByUserId(Long userId);
    Optional<Mentor> findByUserId(Long userId);
    List<Mentor> findByStatus(Mentor.MentorStatus status);
}
