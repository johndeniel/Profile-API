package com.profile.api.fileStore.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class FileStoreResponseDto {

    private UUID id;
    private UUID uploaderId;
    private String blobUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
