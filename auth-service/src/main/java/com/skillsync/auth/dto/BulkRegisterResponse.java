package com.skillsync.auth.dto;

import java.util.List;
import java.util.UUID;

public record BulkRegisterResponse(
        int requested,
        int created,
        int skipped,
        List<CreatedUser> users
) {
    public record CreatedUser(UUID userId, String email) {
    }
}

