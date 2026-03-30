package com.skillsync.mentor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.mentor.entity.Mentor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MentorResponse {

    private final Long id;
    private final Long userId;
    private final String name;
    private final String email;
    private final String bio;
    private final Integer experienceYears;
    private final Double hourlyRate;
    private final String availability;
    private final Double rating;
    private final Integer totalReviews;
    private final String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public MentorResponse(Mentor m) {
        this.id = m.getId();
        this.userId = m.getUserId();
        this.name = m.getName();
        this.email = m.getEmail();
        this.bio = m.getBio();
        this.experienceYears = m.getExperienceYears();
        this.hourlyRate = m.getHourlyRate();
        this.availability = m.getAvailability();
        this.rating = m.getRating();
        this.totalReviews = m.getTotalReviews();
        this.status = m.getStatus() != null ? m.getStatus().name() : null;
        this.createdAt = m.getCreatedAt();
    }
}
