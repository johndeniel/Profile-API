package com.profile.api.professionalexperience.mapper;

import com.profile.api.professionalexperience.dto.ProfessionalExperienceRequestDto;
import com.profile.api.professionalexperience.dto.ProfessionalExperienceResponseDto;
import com.profile.api.professionalexperience.model.ProfessionalExperience;

public final class ProfessionalExperienceMapper {

    private ProfessionalExperienceMapper() {}

    public static ProfessionalExperience toEntity(ProfessionalExperienceRequestDto dto) {
        ProfessionalExperience entity = new ProfessionalExperience();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(ProfessionalExperience entity, ProfessionalExperienceRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(ProfessionalExperience entity, ProfessionalExperienceRequestDto dto) {
        if (dto.getUploaderId() != null) entity.setUploaderId(dto.getUploaderId());
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getCompany() != null) entity.setCompany(dto.getCompany());
        if (dto.getType() != null) entity.setType(dto.getType());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getStartDate() != null) entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }

    public static ProfessionalExperienceResponseDto toResponseDto(ProfessionalExperience entity) {
        ProfessionalExperienceResponseDto dto = new ProfessionalExperienceResponseDto();
        dto.setId(entity.getId());
        dto.setUploaderId(entity.getUploaderId());
        dto.setTitle(entity.getTitle());
        dto.setCompany(entity.getCompany());
        dto.setType(entity.getType());
        dto.setLocation(entity.getLocation());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
