package com.skillsync.mentor.service;

import com.skillsync.mentor.client.SkillServiceClient;
import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.dto.MentorResponse;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import com.skillsync.mentor.service.impl.MentorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @Mock private MentorRepository mentorRepository;
    @Mock private MentorSkillRepository mentorSkillRepository;
    @Mock private SkillServiceClient skillServiceClient;

    @InjectMocks
    private MentorServiceImpl mentorService;

    @Test
    void applyAsMentorAddsValidatedSkills() {
        MentorApplicationRequest request = new MentorApplicationRequest();
        request.setUserId(4L);
        request.setName("Riya");
        request.setExperienceYears(5);
        request.setHourlyRate(750.0);
        request.setSkillIds(List.of(10L));

        when(mentorRepository.existsByUserId(4L)).thenReturn(false);
        when(mentorRepository.save(any(Mentor.class))).thenAnswer(inv -> {
            Mentor m = inv.getArgument(0);
            m.setId(20L);
            return m;
        });
        when(skillServiceClient.getSkillById(10L)).thenReturn(Map.of("name", "Spring Boot"));

        MentorResponse response = mentorService.applyAsMentor(request);

        ArgumentCaptor<MentorSkill> captor = ArgumentCaptor.forClass(MentorSkill.class);
        verify(mentorSkillRepository).save(captor.capture());
        assertEquals(20L, response.getId());
        assertEquals("Spring Boot", captor.getValue().getSkillName());
    }

    @Test
    void updateRatingRejectsOutOfRangeValues() {
        assertThrows(BadRequestException.class, () -> mentorService.updateRating(1L, 5.5, 2));
    }

    @Test
    void updateRatingPersistsValidValues() {
        Mentor mentor = new Mentor();
        mentor.setId(3L);
        when(mentorRepository.findById(3L)).thenReturn(Optional.of(mentor));

        mentorService.updateRating(3L, 4.8, 12);

        verify(mentorRepository).save(eq(mentor));
        assertEquals(4.8, mentor.getRating());
        assertEquals(12, mentor.getTotalReviews());
    }
}
