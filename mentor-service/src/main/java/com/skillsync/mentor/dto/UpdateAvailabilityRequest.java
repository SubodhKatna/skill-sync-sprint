package com.skillsync.mentor.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvailabilityRequest {

    @NotBlank(message = "Availability is required")
    private String availability;
}
