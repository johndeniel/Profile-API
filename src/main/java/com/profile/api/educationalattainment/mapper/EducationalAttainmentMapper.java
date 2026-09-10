package com.profile.api.educationalattainment.mapper;

import com.profile.api.educationalattainment.dto.EducationalAttainmentRequestDto;
import com.profile.api.educationalattainment.dto.EducationalAttainmentResponseDto;
import com.profile.api.educationalattainment.model.EducationalAttainment;

public final class EducationalAttainmentMapper {

    private EducationalAttainmentMapper() {}

    public static EducationalAttainment toEntity(EducationalAttainmentRequestDto dto) {
        EducationalAttainment entity = new EducationalAttainment();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(EducationalAttainment entity, EducationalAttainmentRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(EducationalAttainment entity, EducationalAttainmentRequestDto dto) {
        if (dto.getUploaderId() != null) entity.setUploaderId(dto.getUploaderId());
        if (dto.getInstitution() != null) entity.setInstitution(dto.getInstitution());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        if (dto.getAward() != null) entity.setAward(dto.getAward());
        if (dto.getDegree() != null) entity.setDegree(dto.getDegree());
        if (dto.getField() != null) entity.setField(dto.getField());
    }

    public static EducationalAttainmentResponseDto toResponseDto(EducationalAttainment entity) {
        EducationalAttainmentResponseDto dto = new EducationalAttainmentResponseDto();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setInstitution(entity.getInstitution());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setAward(entity.getAward());
        dto.setDegree(entity.getDegree());
        dto.setField(entity.getField());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
