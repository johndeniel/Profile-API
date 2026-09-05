package com.profile.api.licensecertificate.mapper;

import com.profile.api.licensecertificate.dto.LicenseCertificateRequestDto;
import com.profile.api.licensecertificate.dto.LicenseCertificateResponseDto;
import com.profile.api.licensecertificate.model.LicenseCertificate;

public final class LicenseCertificateMapper {

    private LicenseCertificateMapper() {}

    public static LicenseCertificate toEntity(LicenseCertificateRequestDto dto) {
        LicenseCertificate entity = new LicenseCertificate();
        applyDtoToEntity(entity, dto);
        return entity;
    }

    public static void updateEntity(LicenseCertificate entity, LicenseCertificateRequestDto dto) {
        applyDtoToEntity(entity, dto);
    }

    private static void applyDtoToEntity(LicenseCertificate entity, LicenseCertificateRequestDto dto) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getIssuer() != null) entity.setIssuer(dto.getIssuer());
        if (dto.getIssued() != null) entity.setIssued(dto.getIssued());
        if (dto.getCredentialId() != null) entity.setCredentialId(dto.getCredentialId());
        if (dto.getBlobUrl() != null) entity.setBlobUrl(dto.getBlobUrl());
    }

    public static LicenseCertificateResponseDto toResponseDto(LicenseCertificate entity) {
        LicenseCertificateResponseDto dto = new LicenseCertificateResponseDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setIssuer(entity.getIssuer());
        dto.setIssued(entity.getIssued());
        dto.setCredentialId(entity.getCredentialId());
        dto.setBlobUrl(entity.getBlobUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
