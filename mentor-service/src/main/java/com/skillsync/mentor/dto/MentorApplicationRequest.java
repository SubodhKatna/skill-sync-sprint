package com.skillsync.mentor.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class MentorApplicationRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String name;

    private String email;
    private String bio;

    @Min(0)
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = false)
    private Double hourlyRate;

    private List<Long> skillIds;
}
