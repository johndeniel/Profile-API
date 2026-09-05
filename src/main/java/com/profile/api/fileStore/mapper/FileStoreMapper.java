package com.profile.api.fileStore.mapper;

import com.profile.api.fileStore.dto.FileStoreResponseDto;
import com.profile.api.fileStore.model.FileStore;

public final class FileStoreMapper {

    private FileStoreMapper() {}

    public static FileStoreResponseDto toResponseDto(FileStore entity) {
        FileStoreResponseDto dto = new FileStoreResponseDto();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setBlobUrl(entity.getBlobUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
