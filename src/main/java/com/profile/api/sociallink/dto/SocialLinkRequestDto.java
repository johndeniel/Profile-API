package com.profile.api.sociallink.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SocialLinkRequestDto {

    private UUID uploaderId;

    @Pattern(regexp = "^(LINKEDIN|GITHUB|INSTAGRAM|LEETCODE)$", message = "Platform must be LINKEDIN, GITHUB, INSTAGRAM, or LEETCODE")
    private String platform;

    @URL(message = "Platform URL must be a valid URL")
    @Size(max = 2048, message = "Platform URL must not exceed 2048 characters")
    private String platformUrl;
}
