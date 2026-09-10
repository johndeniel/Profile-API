package com.profile.api.educationalattainment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EducationalAttainmentRequestDto {

    @NotNull(message = "Uploader ID is required")
    private UUID uploaderId;

    @NotBlank(message = "Institution is required")
    @Size(max = 255, message = "Institution must not exceed 255 characters")
    private String institution;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must not be in the future")
    private LocalDateTime startDate;

    @PastOrPresent(message = "End date must not be in the future")
    private LocalDateTime endDate;

    @Size(max = 255, message = "Award must not exceed 255 characters")
    private String award;

    @Size(max = 255, message = "Degree must not exceed 255 characters")
    private String degree;

    @Size(max = 255, message = "Field must not exceed 255 characters")
    private String field;
}
