package com.profile.api.personalinformation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PersonalInformationResponseDto {

    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String headline;
    private String profileImageUrl;
    private String emailAddress;
    private String phoneNumber;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
