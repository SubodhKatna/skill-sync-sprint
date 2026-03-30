package com.skillsync.mentor.controller;

import com.skillsync.mentor.dto.*;
import com.skillsync.mentor.service.MentorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentors")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/apply")
    public ResponseEntity<MentorResponse> applyAsMentor(@Valid @RequestBody MentorApplicationRequest request) {
        return new ResponseEntity<>(mentorService.applyAsMentor(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MentorResponse>> getAllMentors() {
        return ResponseEntity.ok(mentorService.getAllMentors());
    }

    @GetMapping("/all")
    public ResponseEntity<List<MentorResponse>> getAllMentorsIncludingPending() {
        return ResponseEntity.ok(mentorService.getAllMentorsIncludingPending());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MentorResponse> getMentorById(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<MentorResponse> getMentorByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(mentorService.getMentorByUserId(userId));
    }

    @PutMapping("/{id}/availability")
    public ResponseEntity<MentorResponse> updateAvailability(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateAvailabilityRequest request) {
        return ResponseEntity.ok(mentorService.updateAvailability(id, request.getAvailability()));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<MentorResponse> approveMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.approveMentor(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<MentorResponse> rejectMentor(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.rejectMentor(id));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<MentorSkillResponse>> getMentorSkills(@PathVariable Long id) {
        return ResponseEntity.ok(mentorService.getMentorSkills(id));
    }

    @PutMapping("/{id}/rating")
    public ResponseEntity<Void> updateRating(@PathVariable Long id,
                                              @Valid @RequestBody UpdateRatingRequest request) {
        mentorService.updateRating(id, request.getRating(), request.getTotalReviews());
        return ResponseEntity.ok().build();
    }
}
