package com.skillsync.mentor.service.impl;

import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.exception.ConflictException;
import com.skillsync.mentor.exception.ResourceNotFoundException;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import com.skillsync.mentor.service.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class MentorServiceImpl implements MentorService {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private MentorSkillRepository mentorSkillRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${skill.service.url}")
    private String skillServiceUrl;

    @Override
    @Transactional
    public Mentor applyAsMentor(MentorApplicationRequest request) {
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

        // Add skills via inter-service call to validate
        if (request.getSkillIds() != null) {
            for (Long skillId : request.getSkillIds().stream().filter(Objects::nonNull).distinct().toList()) {
                try {
                    // Call skill-service to validate skill exists
                    Map<?, ?> skillData = restTemplate.getForObject(
                            skillServiceUrl + "/skills/" + skillId, Map.class);
                    if (skillData != null) {
                        MentorSkill mentorSkill = new MentorSkill();
                        mentorSkill.setMentorId(mentor.getId());
                        mentorSkill.setSkillId(skillId);
                        mentorSkill.setSkillName((String) skillData.get("name"));
                        mentorSkillRepository.save(mentorSkill);
                    }
                } catch (Exception e) {
                    // Skill service unavailable, skip skill validation
                }
            }
        }

        return mentor;
    }

    @Override
    public List<Mentor> getAllMentors() {
        return mentorRepository.findByStatus(Mentor.MentorStatus.APPROVED);
    }

    @Override
    public List<Mentor> getAllMentorsIncludingPending() {
        return mentorRepository.findAll();
    }

    @Override
    public Mentor getMentorById(Long id) {
        return mentorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found with id: " + id));
    }

    @Override
    public Mentor getMentorByUserId(Long userId) {
        return mentorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Mentor not found for user: " + userId));
    }

    @Override
    public Mentor updateAvailability(Long id, String availability) {
        Mentor mentor = getMentorById(id);
        mentor.setAvailability(availability.trim());
        return mentorRepository.save(mentor);
    }

    @Override
    public Mentor approveMentor(Long id) {
        Mentor mentor = getMentorById(id);
        mentor.setStatus(Mentor.MentorStatus.APPROVED);
        return mentorRepository.save(mentor);
    }

    @Override
    public Mentor rejectMentor(Long id) {
        Mentor mentor = getMentorById(id);
        mentor.setStatus(Mentor.MentorStatus.REJECTED);
        return mentorRepository.save(mentor);
    }

    @Override
    public void updateRating(Long mentorId, Double newRating, Integer totalReviews) {
        if (newRating == null || newRating < 0 || newRating > 5) {
            throw new BadRequestException("Rating must be between 0 and 5");
        }
        if (totalReviews == null || totalReviews < 0) {
            throw new BadRequestException("Total reviews must be zero or greater");
        }
        Mentor mentor = getMentorById(mentorId);
        mentor.setRating(newRating);
        mentor.setTotalReviews(totalReviews);
        mentorRepository.save(mentor);
    }

    @Override
    public List<MentorSkill> getMentorSkills(Long mentorId) {
        return mentorSkillRepository.findByMentorId(mentorId);
    }
}
