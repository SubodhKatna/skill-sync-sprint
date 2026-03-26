package com.skillsync.auth.service;

import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.User;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(Long userId);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUser(User user);
}
