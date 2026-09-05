package com.profile.api.licensecertificate.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class LicenseCertificateResponseDto {

    private UUID id;
    private String title;
    private String issuer;
    private LocalDateTime issued;
    private String credentialId;
    private String credentialUrl;
    private String description;
    private String blobUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
