package com.skillsync.mentor.service;

import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;

import java.util.List;

public interface MentorService {
    Mentor applyAsMentor(MentorApplicationRequest request);
    List<Mentor> getAllMentors();
    List<Mentor> getAllMentorsIncludingPending();
    Mentor getMentorById(Long id);
    Mentor getMentorByUserId(Long userId);
    Mentor updateAvailability(Long id, String availability);
    Mentor approveMentor(Long id);
    Mentor rejectMentor(Long id);
    void updateRating(Long mentorId, Double newRating, Integer totalReviews);
    List<MentorSkill> getMentorSkills(Long mentorId);
}
