package com.profile.api.personalinformation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class PersonalInformationRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Size(max = 300, message = "Headline must not exceed 300 characters")
    private String headline;

    @URL(message = "Profile image URL must be a valid URL")
    @Size(max = 2048, message = "Profile image URL must not exceed 2048 characters")
    private String profileImageUrl;

    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String emailAddress;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
}
