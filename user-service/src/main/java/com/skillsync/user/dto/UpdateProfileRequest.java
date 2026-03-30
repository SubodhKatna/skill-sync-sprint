package com.skillsync.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String bio;
    private String phone;
    private String profileImageUrl;
    private String email;
}
