package com.profile.api.professionalexperience.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProfessionalExperienceResponseDto {

    private UUID id;
    private UUID uploaderId;
    private String title;
    private String company;
    private String type;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
