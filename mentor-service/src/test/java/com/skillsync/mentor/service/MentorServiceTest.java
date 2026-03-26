package com.skillsync.mentor.service;

import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.repository.MentorRepository;
import com.skillsync.mentor.repository.MentorSkillRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.skillsync.mentor.service.impl.MentorServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private MentorSkillRepository mentorSkillRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MentorServiceImpl mentorService;

    @Test
    void applyAsMentorAddsValidatedSkills() {
        ReflectionTestUtils.setField(mentorService, "skillServiceUrl", "http://skill-service");

        MentorApplicationRequest request = new MentorApplicationRequest();
        request.setUserId(4L);
        request.setName("Riya");
        request.setExperienceYears(5);
        request.setHourlyRate(750.0);
        request.setSkillIds(List.of(10L));

        when(mentorRepository.existsByUserId(4L)).thenReturn(false);
        when(mentorRepository.save(any(Mentor.class))).thenAnswer(invocation -> {
            Mentor mentor = invocation.getArgument(0);
            mentor.setId(20L);
            return mentor;
        });
        when(restTemplate.getForObject("http://skill-service/skills/10", Map.class))
                .thenReturn(Map.of("name", "Spring Boot"));

        Mentor mentor = mentorService.applyAsMentor(request);

        ArgumentCaptor<MentorSkill> skillCaptor = ArgumentCaptor.forClass(MentorSkill.class);
        verify(mentorSkillRepository).save(skillCaptor.capture());
        assertEquals(20L, mentor.getId());
        assertEquals("Spring Boot", skillCaptor.getValue().getSkillName());
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
