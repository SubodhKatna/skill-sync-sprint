package com.skillsync.auth.service;

import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.User;
import com.skillsync.auth.exception.BadRequestException;
import com.skillsync.auth.repository.RefreshTokenRepository;
import com.skillsync.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void createRefreshTokenReusesExistingTokenRow() {
        org.springframework.test.util.ReflectionTestUtils.setField(refreshTokenService, "refreshTokenExpiration", 60000L);

        User user = new User();
        user.setId(7L);

        RefreshToken existingToken = new RefreshToken();
        existingToken.setId(3L);
        existingToken.setUser(user);
        existingToken.setToken("old-token");

        when(userRepository.findByIdForUpdate(7L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.findByUser(user)).thenReturn(Optional.of(existingToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RefreshToken savedToken = refreshTokenService.createRefreshToken(7L);

        assertSame(existingToken, savedToken);
        verify(refreshTokenRepository).save(existingToken);
    }

    @Test
    void verifyExpirationReturnsTokenWhenStillValid() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().plusSeconds(60));

        RefreshToken verified = refreshTokenService.verifyExpiration(token);

        assertSame(token, verified);
    }

    @Test
    void verifyExpirationDeletesExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60));

        assertThrows(BadRequestException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }
}
