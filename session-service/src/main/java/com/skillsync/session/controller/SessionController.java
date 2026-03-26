package com.skillsync.session.controller;

import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;
import com.skillsync.session.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody SessionRequest request) {
        return new ResponseEntity<>(sessionService.createSession(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponse> getSessionById(@PathVariable Long id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    @GetMapping("/mentor/{mentorId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByMentorId(@PathVariable Long mentorId) {
        return ResponseEntity.ok(sessionService.getSessionsByMentorId(mentorId));
    }

    @GetMapping("/mentee/{menteeId}")
    public ResponseEntity<List<SessionResponse>> getSessionsByMenteeId(@PathVariable Long menteeId) {
        return ResponseEntity.ok(sessionService.getSessionsByMenteeId(menteeId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SessionResponse> updateSessionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(sessionService.updateSessionStatus(id, status));
    }
}
