package com.profile.api.personalinformation.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "personal_information", indexes = {
        @Index(name = "idx_pi_first_name", columnList = "first_name"),
        @Index(name = "idx_pi_middle_name", columnList = "middle_name"),
        @Index(name = "idx_pi_last_name", columnList = "last_name"),
        @Index(name = "idx_pi_headline", columnList = "headline"),
        @Index(name = "idx_pi_email_address", columnList = "email_address"),
        @Index(name = "idx_pi_phone_number", columnList = "phone_number"),
        @Index(name = "idx_pi_location", columnList = "location"),
        @Index(name = "idx_pi_blob_id", columnList = "blob_id")
})
@Getter
@Setter
public class PersonalInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "headline", length = 300)
    private String headline;

    @Column(name = "blob_url", columnDefinition = "TEXT")
    private String blobUrl;

    @Column(name = "blob_id")
    private UUID blobId;

    @Column(name = "email_address", length = 255)
    private String emailAddress;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "location", length = 255)
    private String location;

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
