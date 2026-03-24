package com.skillsync.auth.repository;

import com.skillsync.auth.entity.User;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
 * Finds a user by their email address.
 *
 * @param email the user's email address to look up
 * @return an Optional containing the matching User if found, or an empty Optional otherwise
 */
Optional<User> findByEmail(String email);
    /**
 * Check whether a user with the specified email exists.
 *
 * @param email the email address to look up
 * @return `true` if a user with the given email exists, `false` otherwise
 */
boolean existsByEmail(String email);

    /**
     * Load a User by id and acquire a pessimistic write lock on its database row.
     *
     * The selected row is locked with a pessimistic write lock for the duration of the surrounding transaction.
     *
     * @param id the identifier of the user to retrieve
     * @return an Optional containing the matching User if found, otherwise an empty Optional
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.id = :id")
    Optional<User> findByIdForUpdate(@Param("id") Long id);
}
