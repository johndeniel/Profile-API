package com.profile.api.educationalattainment.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.educationalattainment.dto.EducationalAttainmentRequestDto;
import com.profile.api.educationalattainment.dto.EducationalAttainmentResponseDto;
import com.profile.api.educationalattainment.service.EducationalAttainmentService;
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
@RequestMapping("/v1/educational-attainment")
@Tag(name = "Educational Attainment", description = "Educational Attainment Management")
public class EducationalAttainmentController {

    private final EducationalAttainmentService educationalAttainmentService;

    public EducationalAttainmentController(EducationalAttainmentService educationalAttainmentService) {
        this.educationalAttainmentService = educationalAttainmentService;
    }

    @Operation(parameters = {
            @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, description = "Unique key for idempotent request (max 64 chars)", required = true, schema = @Schema(maxLength = 64))
    })
    @PostMapping
    public ResponseEntity<EducationalAttainmentResponseDto> createEducationalAttainment(
            @Valid @RequestBody EducationalAttainmentRequestDto requestDto) {
        EducationalAttainmentResponseDto created = educationalAttainmentService.createEducationalAttainment(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<EducationalAttainmentResponseDto>> getAllEducationalAttainments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID uploaderId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String institution,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) String field,
            @RequestParam(required = false) String award) {

        PaginatedResponseDto<EducationalAttainmentResponseDto> result =
                educationalAttainmentService.getEducationalAttainments(page, size, sortBy, sortDirection, id, uploaderId,
                        search, institution, degree, field, award);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EducationalAttainmentResponseDto> updateEducationalAttainment(
            @PathVariable UUID id,
            @RequestBody EducationalAttainmentRequestDto requestDto) {
        EducationalAttainmentResponseDto updated = educationalAttainmentService.updateEducationalAttainment(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducationalAttainment(@PathVariable UUID id) {
        educationalAttainmentService.deleteEducationalAttainment(id);
        return ResponseEntity.noContent().build();
    }
}
