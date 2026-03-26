package com.skillsync.session.service;

import com.skillsync.session.dto.SessionRequest;
import com.skillsync.session.dto.SessionResponse;

import java.util.List;

public interface SessionService {
    SessionResponse createSession(SessionRequest request);
    List<SessionResponse> getSessionsByMentorId(Long mentorId);
    List<SessionResponse> getSessionsByMenteeId(Long menteeId);
    SessionResponse getSessionById(Long id);
    SessionResponse updateSessionStatus(Long id, String status);
}
