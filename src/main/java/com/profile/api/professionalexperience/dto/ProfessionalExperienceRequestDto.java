package com.profile.api.professionalexperience.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ProfessionalExperienceRequestDto {

    @NotNull(message = "Uploader ID is required")
    private UUID uploaderId;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Company is required")
    @Size(max = 255, message = "Company must not exceed 255 characters")
    private String company;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "^(FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP|FREELANCE|SELF_EMPLOYED)$", message = "Type must be one of: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE, SELF_EMPLOYED")
    private String type;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must not be in the future")
    private LocalDateTime startDate;

    @PastOrPresent(message = "End date must not be in the future")
    private LocalDateTime endDate;
}
