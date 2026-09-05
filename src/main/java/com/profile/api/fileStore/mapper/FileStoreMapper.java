package com.profile.api.fileStore.mapper;

import com.profile.api.fileStore.dto.FileStoreResponseDto;
import com.profile.api.fileStore.model.FileStore;

public final class FileStoreMapper {

    private FileStoreMapper() {}

    public static FileStoreResponseDto toResponseDto(FileStore entity) {
        if (entity == null) {
            return null;
        }
        return new FileStoreResponseDto(
                entity.getId(),
                entity.getUploaderId(),
                entity.getBlobUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
