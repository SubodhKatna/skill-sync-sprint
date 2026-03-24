package com.skillsync.auth.repository;

import com.skillsync.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
 * Finds a Role entity by its enum name.
 *
 * @param name the Role.RoleName enum value to look up
 * @return an Optional containing the matching Role if found, or empty if no match exists
 */
Optional<Role> findByName(Role.RoleName name);
}
