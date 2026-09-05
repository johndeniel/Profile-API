package com.profile.api.licensecertificate.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "license_certificate")
@Getter
@Setter
public class LicenseCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "issuer", nullable = false, length = 255)
    private String issuer;

    @Column(name = "issued", nullable = false)
    private LocalDateTime issued;

    @Column(name = "credential_id", length = 255)
    private String credentialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false, length = 10)
    private LicenseLevel level;

    @Column(name = "credential_url", length = 2048)
    private String credentialUrl;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "blob_url", columnDefinition = "TEXT")
    private String blobUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
