package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.client.SkillServiceClient;
import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.dto.MentorResponse;
import com.skillsync.mentor.dto.MentorSkillResponse;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.exception.ConflictException;
import com.skillsync.mentor.exception.ResourceNotFoundException;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import com.skillsync.mentor.service.MentorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final MentorSkillRepository mentorSkillRepository;
    private final SkillServiceClient skillServiceClient;

    @Override
    @Transactional
    public MentorResponse applyAsMentor(MentorApplicationRequest request) {
        if (mentorRepository.existsByUserId(request.getUserId())) {
            throw new ConflictException("Mentor application already exists for user: " + request.getUserId());
        }

        Mentor mentor = new Mentor();
        mentor.setUserId(request.getUserId());
        mentor.setName(request.getName().trim());
        mentor.setEmail(request.getEmail() == null ? null : request.getEmail().trim());
        mentor.setBio(request.getBio() == null ? null : request.getBio().trim());
        mentor.setExperienceYears(request.getExperienceYears());
        mentor.setHourlyRate(request.getHourlyRate());
        mentor = mentorRepository.save(mentor);

        if (request.getSkillIds() != null) {
            for (Long skillId : request.getSkillIds().stream().filter(Objects::nonNull).distinct().toList()) {
                try {
                    Map<String, Object> skillData = skillServiceClient.getSkillById(skillId);
                    MentorSkill mentorSkill = new MentorSkill();
                    mentorSkill.setMentorId(mentor.getId());
                    mentorSkill.setSkillId(skillId);
                    mentorSkill.setSkillName((String) skillData.get("name"));
                    mentorSkillRepository.save(mentorSkill);
                } catch (Exception e) {
                    log.warn("Skill {} not found or skill-service unavailable, skipping", skillId);
                }
            }
        }

        return new MentorResponse(mentor);
    }

    @Override
    public List<MentorResponse> getAllMentors() {
        return mentorRepository.findByStatus(Mentor.MentorStatus.APPROVED)
                .stream().map(MentorResponse::new).toList();
    }

    @Override
    public List<MentorResponse> getAllMentorsIncludingPending() {
        return mentorRepository.findAll().stream().map(MentorResponse::new).toList();
    }

    @Override
    public MentorResponse getMentorById(Long id) {
        return new MentorResponse(findById(id));
    }

    @Override
    public MentorResponse getMentorByUserId(Long userId) {
        return new MentorResponse(mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found for user: " + userId)));
    }

    @Override
    public MentorResponse updateAvailability(Long id, String availability) {
        Mentor mentor = findById(id);
        mentor.setAvailability(availability.trim());
        return new MentorResponse(mentorRepository.save(mentor));
    }

    @Override
    public MentorResponse approveMentor(Long id) {
        Mentor mentor = findById(id);
        mentor.setStatus(Mentor.MentorStatus.APPROVED);
        return new MentorResponse(mentorRepository.save(mentor));
    }

    @Override
    public MentorResponse rejectMentor(Long id) {
        Mentor mentor = findById(id);
        mentor.setStatus(Mentor.MentorStatus.REJECTED);
        return new MentorResponse(mentorRepository.save(mentor));
    }

    @Override
    public void updateRating(Long mentorId, Double newRating, Integer totalReviews) {
        if (newRating == null || newRating < 0 || newRating > 5)
            throw new BadRequestException("Rating must be between 0 and 5");
        if (totalReviews == null || totalReviews < 0)
            throw new BadRequestException("Total reviews must be zero or greater");
        Mentor mentor = findById(mentorId);
        mentor.setRating(newRating);
        mentor.setTotalReviews(totalReviews);
        mentorRepository.save(mentor);
    }

    @Override
    public List<MentorSkillResponse> getMentorSkills(Long mentorId) {
        return mentorSkillRepository.findByMentorId(mentorId)
                .stream().map(MentorSkillResponse::new).toList();
    }

    private Mentor findById(Long id) {
        return mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + id));
    }
}
