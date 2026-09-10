package com.profile.api.professionalexperience.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.professionalexperience.dto.ProfessionalExperienceRequestDto;
import com.profile.api.professionalexperience.dto.ProfessionalExperienceResponseDto;
import com.profile.api.professionalexperience.service.ProfessionalExperienceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/professional-experience")
@Tag(name = "Professional Experience", description = "Professional Experience Management")
public class ProfessionalExperienceController {

    private final ProfessionalExperienceService professionalExperienceService;

    public ProfessionalExperienceController(ProfessionalExperienceService professionalExperienceService) {
        this.professionalExperienceService = professionalExperienceService;
    }

    @Operation(parameters = {
            @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, description = "Unique key for idempotent request (max 64 chars)", required = true, schema = @Schema(maxLength = 64))
    })
    @PostMapping
    public ResponseEntity<ProfessionalExperienceResponseDto> createProfessionalExperience(
            @Valid @RequestBody ProfessionalExperienceRequestDto requestDto) {
        ProfessionalExperienceResponseDto created = professionalExperienceService.createProfessionalExperience(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<ProfessionalExperienceResponseDto>> getAllProfessionalExperiences(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID uploaderId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {

        PaginatedResponseDto<ProfessionalExperienceResponseDto> result =
                professionalExperienceService.getProfessionalExperiences(page, size, sortBy, sortDirection, id, uploaderId,
                        search, title, company, type, location);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalExperienceResponseDto> updateProfessionalExperience(
            @PathVariable UUID id,
            @RequestBody ProfessionalExperienceRequestDto requestDto) {
        ProfessionalExperienceResponseDto updated = professionalExperienceService.updateProfessionalExperience(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessionalExperience(@PathVariable UUID id) {
        professionalExperienceService.deleteProfessionalExperience(id);
        return ResponseEntity.noContent().build();
    }
}
