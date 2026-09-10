package com.profile.api.educationalattainment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class EducationalAttainmentResponseDto {

    private UUID id;
    private UUID uploaderId;
    private String institution;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String award;
    private String degree;
    private String field;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
