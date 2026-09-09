package com.profile.api.curriculumvitae.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "curriculum_vitae", indexes = {
        @Index(name = "idx_cv_uploader_id", columnList = "uploader_id"),
        @Index(name = "idx_cv_issued", columnList = "issued"),
        @Index(name = "idx_cv_blob_id", columnList = "blob_id")
})
@Getter
@Setter
public class CurriculumVitae {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "uploader_id", nullable = false)
    private UUID uploaderId;

    @Column(name = "blob_url", columnDefinition = "TEXT")
    private String blobUrl;

    @Column(name = "blob_id")
    private UUID blobId;

    @Column(name = "issued", nullable = false)
    private LocalDateTime issued;

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
