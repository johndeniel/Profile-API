package com.profile.api.curriculumvitae.mapper;

import com.profile.api.curriculumvitae.dto.CurriculumVitaeRequestDto;
import com.profile.api.curriculumvitae.dto.CurriculumVitaeResponseDto;
import com.profile.api.curriculumvitae.model.CurriculumVitae;

public final class CurriculumVitaeMapper {

    private CurriculumVitaeMapper() {}

    public static CurriculumVitae toEntity(CurriculumVitaeRequestDto dto) {
        CurriculumVitae entity = new CurriculumVitae();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(CurriculumVitae entity, CurriculumVitaeRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(CurriculumVitae entity, CurriculumVitaeRequestDto dto) {
        if (dto.getUploaderId() != null) entity.setUploaderId(dto.getUploaderId());
        if (dto.getBlobUrl() != null) entity.setBlobUrl(dto.getBlobUrl());
        if (dto.getBlobId() != null) entity.setBlobId(dto.getBlobId());
        if (dto.getIssued() != null) entity.setIssued(dto.getIssued());
    }

    public static CurriculumVitaeResponseDto toResponseDto(CurriculumVitae entity) {
        CurriculumVitaeResponseDto dto = new CurriculumVitaeResponseDto();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setBlobUrl(entity.getBlobUrl());
        dto.setBlobId(entity.getBlobId());
        dto.setIssued(entity.getIssued());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
