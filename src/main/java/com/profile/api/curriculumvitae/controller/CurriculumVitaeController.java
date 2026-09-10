package com.profile.api.curriculumvitae.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.curriculumvitae.dto.CurriculumVitaeRequestDto;
import com.profile.api.curriculumvitae.dto.CurriculumVitaeResponseDto;
import com.profile.api.curriculumvitae.service.CurriculumVitaeService;
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
@RequestMapping("/v1/curriculum-vitae")
@Tag(name = "Curriculum Vitae", description = "Curriculum Vitae Management")
public class CurriculumVitaeController {

    private final CurriculumVitaeService curriculumVitaeService;

    public CurriculumVitaeController(CurriculumVitaeService curriculumVitaeService) {
        this.curriculumVitaeService = curriculumVitaeService;
    }

    @Operation(parameters = {
            @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, description = "Unique key for idempotent request (max 64 chars)", required = true, schema = @Schema(maxLength = 64))
    })
    @PostMapping
    public ResponseEntity<CurriculumVitaeResponseDto> createCurriculumVitae(
            @Valid @RequestBody CurriculumVitaeRequestDto requestDto) {
        CurriculumVitaeResponseDto created = curriculumVitaeService.createCurriculumVitae(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<CurriculumVitaeResponseDto>> getAllCurriculumVitaes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID uploaderId) {

        PaginatedResponseDto<CurriculumVitaeResponseDto> result =
                curriculumVitaeService.getCurriculumVitaes(page, size, sortBy, sortDirection, id, uploaderId);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurriculumVitaeResponseDto> updateCurriculumVitae(
            @PathVariable UUID id,
            @RequestBody CurriculumVitaeRequestDto requestDto) {
        CurriculumVitaeResponseDto updated = curriculumVitaeService.updateCurriculumVitae(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurriculumVitae(@PathVariable UUID id) {
        curriculumVitaeService.deleteCurriculumVitae(id);
        return ResponseEntity.noContent().build();
    }
}
