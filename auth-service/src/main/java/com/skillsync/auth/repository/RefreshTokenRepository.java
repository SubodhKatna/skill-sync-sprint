package com.skillsync.auth.repository;

import com.skillsync.auth.entity.RefreshToken;
import com.skillsync.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    /**
 * Finds a RefreshToken entity by its token string.
 *
 * @param token the refresh token string to look up
 * @return an {@link Optional} containing the matching {@link RefreshToken} if present, or {@link Optional#empty()} otherwise
 */
Optional<RefreshToken> findByToken(String token);
    /**
 * Finds the refresh token associated with a given user.
 *
 * @param user the user whose associated refresh token is sought
 * @return an Optional containing the user's RefreshToken if found, otherwise an empty Optional
 */
Optional<RefreshToken> findByUser(User user);

    /**
     * Deletes all refresh tokens associated with the specified user.
     *
     * @param user the user whose refresh tokens will be deleted
     */
    @Modifying
    @Transactional
    void deleteByUser(User user);
}
