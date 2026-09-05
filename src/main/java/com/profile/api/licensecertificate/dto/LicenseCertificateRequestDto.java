package com.profile.api.licensecertificate.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class LicenseCertificateRequestDto {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Issuer is required")
    @Size(max = 255, message = "Issuer must not exceed 255 characters")
    private String issuer;

    @NotNull(message = "Issued date is required")
    private LocalDateTime issued;

    @Size(max = 255, message = "Credential ID must not exceed 255 characters")
    private String credentialId;

    @Size(max = 2048, message = "Credential URL must not exceed 2048 characters")
    private String credentialUrl;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 2048, message = "Blob URL must not exceed 2048 characters")
    private String blobUrl;
}
