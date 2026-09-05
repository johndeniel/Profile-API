package com.profile.api.licensecertificate.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.licensecertificate.dto.LicenseCertificateRequestDto;
import com.profile.api.licensecertificate.dto.LicenseCertificateResponseDto;
import com.profile.api.licensecertificate.service.LicenseCertificateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/license-certificate")
@Tag(name = "License Certificate", description = "License and Certificate Management")
public class LicenseCertificateController {

    private final LicenseCertificateService licenseCertificateService;

    public LicenseCertificateController(LicenseCertificateService licenseCertificateService) {
        this.licenseCertificateService = licenseCertificateService;
    }

    @PostMapping
    public ResponseEntity<LicenseCertificateResponseDto> createLicenseCertificate(
            @Valid @RequestBody LicenseCertificateRequestDto requestDto) {
        LicenseCertificateResponseDto created = licenseCertificateService.createLicenseCertificate(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<LicenseCertificateResponseDto>> getAllLicenseCertificates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String issuer,
            @RequestParam(required = false) String level) {

        PaginatedResponseDto<LicenseCertificateResponseDto> result =
                licenseCertificateService.getLicenseCertificates(page, size, sortBy, sortDirection, id, search,
                        title, issuer, level);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LicenseCertificateResponseDto> updateLicenseCertificate(
            @PathVariable UUID id,
            @Valid @RequestBody LicenseCertificateRequestDto requestDto) {
        LicenseCertificateResponseDto updated = licenseCertificateService.updateLicenseCertificate(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLicenseCertificate(@PathVariable UUID id) {
        licenseCertificateService.deleteLicenseCertificate(id);
        return ResponseEntity.noContent().build();
    }
}
