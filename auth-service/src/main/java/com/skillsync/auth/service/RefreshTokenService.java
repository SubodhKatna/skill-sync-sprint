package com.skillsync.auth.service;

import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.exception.ResourceNotFoundException;
import com.skillsync.auth.repository.RefreshTokenRepository;
import com.skillsync.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create or update a refresh token for the specified user and persist it.
     *
     * @param userId the id of the user to associate with the refresh token
     * @return the persisted {@code RefreshToken} containing a new token string and expiry date
     * @throws ResourceNotFoundException if no user exists with the given id
     */
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseGet(RefreshToken::new);
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Locate a refresh token by its token string.
     *
     * @param token the refresh token string to look up
     * @return an Optional containing the matching RefreshToken if present, empty otherwise
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Validates that the provided refresh token is not expired.
     *
     * @param token the refresh token to validate
     * @return the same `token` if it has not expired
     * @throws BadRequestException if the token's expiry date is before the current time; the token is deleted before the exception is thrown
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token has expired. Please login again.");
        }
        return token;
    }

    /**
     * Delete all refresh tokens associated with the given user.
     *
     * @param user the user whose refresh tokens will be removed
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
