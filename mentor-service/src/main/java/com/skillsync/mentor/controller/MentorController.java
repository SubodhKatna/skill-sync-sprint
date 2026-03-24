package com.skillsync.mentor.controller;

import com.skillsync.mentor.dto.MentorApplicationRequest;
import com.skillsync.mentor.entity.Mentor;
import com.skillsync.mentor.entity.MentorSkill;
import com.skillsync.mentor.exception.BadRequestException;
import com.skillsync.mentor.service.MentorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mentors")
public class MentorController {

    @Autowired
    private MentorService mentorService;

    @PostMapping("/apply")
    public ResponseEntity<Mentor> applyAsMentor(@Valid @RequestBody MentorApplicationRequest request) {
        return ResponseEntity.ok(mentorService.applyAsMentor(request));
    }

    @GetMapping
    public ResponseEntity<List<Mentor>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentors());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Mentor>> getAllMentorsIncludingPending() {
        return ResponseEntity.ok(mentorService.getAllMentorsIncludingPending());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mentor> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Mentor> getMentorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorService.getMentorByUserId(userId));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<Mentor> updateAvailability(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        if (body == null || body.get("availability") == null || body.get("availability").isBlank()) {
            throw new BadRequestException("Availability is required");
        }
        return ResponseEntity.ok(mentorService.updateAvailability(id, body.get("availability")));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<Mentor> approveMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.approveMentor(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<Mentor> rejectMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.rejectMentor(id));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<MentorSkill>> getMentorSkills(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorSkills(id));
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(
            @PathVariable Long id, @RequestBody Map<String, Object> body) {
        if (body == null || !(body.get("rating") instanceof Number) || !(body.get("totalReviews") instanceof Number)) {
            throw new BadRequestException("Rating and totalReviews are required");
        }
        Double rating = ((Number) body.get("rating")).doubleValue();
        Integer totalReviews = ((Number) body.get("totalReviews")).intValue();
        mentorService.updateRating(id, rating, totalReviews);
        return ResponseEntity.ok().build();
    }
}
