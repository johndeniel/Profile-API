package com.profile.api.sociallink.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SocialLinkResponseDto {

    private UUID id;
    private UUID uploaderId;
    private String platform;
    private String platformUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
