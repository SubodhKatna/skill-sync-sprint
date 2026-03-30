package com.skillsync.mentor.service;

import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.dto.MentorResponse;
import com.skillsync.mentor.dto.MentorSkillResponse;

import java.util.List;

public interface MentorService {
    MentorResponse applyAsMentor(MentorApplicationRequest request);
    List<MentorResponse> getAllMentors();
    List<MentorResponse> getAllMentorsIncludingPending();
    MentorResponse getMentorById(Long id);
    MentorResponse getMentorByUserId(Long userId);
    MentorResponse updateAvailability(Long id, String availability);
    MentorResponse approveMentor(Long id);
    MentorResponse rejectMentor(Long id);
    void updateRating(Long mentorId, Double newRating, Integer totalReviews);
    List<MentorSkillResponse> getMentorSkills(Long mentorId);
}
