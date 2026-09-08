package com.profile.api.sociallink.mapper;

import com.profile.api.sociallink.dto.SocialLinkRequestDto;
import com.profile.api.sociallink.dto.SocialLinkResponseDto;
import com.profile.api.sociallink.model.SocialLink;

public final class SocialLinkMapper {

    private SocialLinkMapper() {}

    public static SocialLink toEntity(SocialLinkRequestDto dto) {
        SocialLink entity = new SocialLink();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(SocialLink entity, SocialLinkRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(SocialLink entity, SocialLinkRequestDto dto) {
        if (dto.getUploaderId() != null) entity.setUploaderId(dto.getUploaderId());
        if (dto.getPlatform() != null) entity.setPlatform(dto.getPlatform());
        if (dto.getPlatformUrl() != null) entity.setPlatformUrl(dto.getPlatformUrl());
    }

    public static SocialLinkResponseDto toResponseDto(SocialLink entity) {
        SocialLinkResponseDto dto = new SocialLinkResponseDto();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setPlatform(entity.getPlatform());
        dto.setPlatformUrl(entity.getPlatformUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
