package com.profile.api.educationalattainment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "educational_attainment", indexes = {
        @Index(name = "idx_ea_uploader_id", columnList = "uploader_id"),
        @Index(name = "idx_ea_institution", columnList = "institution"),
        @Index(name = "idx_ea_degree", columnList = "degree"),
        @Index(name = "idx_ea_field", columnList = "field"),
        @Index(name = "idx_ea_award", columnList = "award"),
        @Index(name = "idx_ea_start_date", columnList = "start_date"),
        @Index(name = "idx_ea_end_date", columnList = "end_date")
})
@Getter
@Setter
public class EducationalAttainment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "uploader_id", nullable = false)
    private UUID uploaderId;

    @Column(name = "institution", nullable = false, length = 255)
    private String institution;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "award", length = 255)
    private String award;

    @Column(name = "degree", length = 255)
    private String degree;

    @Column(name = "field", length = 255)
    private String field;

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
