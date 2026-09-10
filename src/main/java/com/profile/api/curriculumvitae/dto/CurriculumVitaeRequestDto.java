package com.profile.api.curriculumvitae.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class CurriculumVitaeRequestDto {

    @NotNull(message = "Uploader ID is required")
    private UUID uploaderId;

    @URL(message = "Blob URL must be a valid URL")
    @Size(max = 2048, message = "Blob URL must not exceed 2048 characters")
    private String blobUrl;

    private UUID blobId;

    @NotNull(message = "Issued date is required")
    @PastOrPresent(message = "Issued date must not be in the future")
    private LocalDateTime issued;
}
