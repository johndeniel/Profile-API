package com.profile.api.curriculumvitae.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CurriculumVitaeResponseDto {

    private UUID id;
    private UUID uploaderId;
    private String blobUrl;
    private UUID blobId;
    private LocalDateTime issued;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
