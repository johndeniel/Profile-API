package com.profile.api.fileStore.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FileStoreResponseDto(
        UUID id,
        UUID uploaderId,
        String blobUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
