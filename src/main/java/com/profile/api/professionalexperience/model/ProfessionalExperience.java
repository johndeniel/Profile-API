package com.profile.api.professionalexperience.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "professional_experience", indexes = {
        @Index(name = "idx_pe_uploader_id", columnList = "uploader_id"),
        @Index(name = "idx_pe_title", columnList = "title"),
        @Index(name = "idx_pe_company", columnList = "company"),
        @Index(name = "idx_pe_type", columnList = "type"),
        @Index(name = "idx_pe_location", columnList = "location"),
        @Index(name = "idx_pe_start_date", columnList = "start_date"),
        @Index(name = "idx_pe_end_date", columnList = "end_date")
})
@Getter
@Setter
public class ProfessionalExperience {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "uploader_id", nullable = false)
    private UUID uploaderId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "company", nullable = false, length = 255)
    private String company;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

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
