package com.profile.api.personalinformation.mapper;

import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.model.PersonalInformation;

public final class PersonalInformationMapper {

    private PersonalInformationMapper() {}

    public static PersonalInformation toEntity(PersonalInformationRequestDto dto) {
        PersonalInformation entity = new PersonalInformation();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(PersonalInformation entity, PersonalInformationRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(PersonalInformation entity, PersonalInformationRequestDto dto) {
        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getMiddleName() != null) entity.setMiddleName(dto.getMiddleName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getHeadline() != null) entity.setHeadline(dto.getHeadline());
        if (dto.getBlobUrl() != null) entity.setBlobUrl(dto.getBlobUrl());
        if (dto.getBlobId() != null) entity.setBlobId(dto.getBlobId());
        if (dto.getEmailAddress() != null) entity.setEmailAddress(dto.getEmailAddress());
        if (dto.getPhoneNumber() != null) entity.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
    }

    public static PersonalInformationResponseDto toResponseDto(PersonalInformation entity) {
        PersonalInformationResponseDto dto = new PersonalInformationResponseDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setHeadline(entity.getHeadline());
        dto.setBlobUrl(entity.getBlobUrl());
        dto.setBlobId(entity.getBlobId());
        dto.setEmailAddress(entity.getEmailAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setLocation(entity.getLocation());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
