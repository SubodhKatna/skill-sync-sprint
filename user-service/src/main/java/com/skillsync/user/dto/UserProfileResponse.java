package com.skillsync.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillsync.user.entity.UserProfile;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserProfileResponse {

    private final Long id;
    private final Long userId;
    private final String name;
    private final String email;
    private final String bio;
    private final String profileImageUrl;
    private final String phone;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    public UserProfileResponse(UserProfile p) {
        this.id = p.getId();
        this.userId = p.getUserId();
        this.name = p.getName();
        this.email = p.getEmail();
        this.bio = p.getBio();
        this.profileImageUrl = p.getProfileImageUrl();
        this.phone = p.getPhone();
        this.createdAt = p.getCreatedAt();
        this.updatedAt = p.getUpdatedAt();
    }
}
