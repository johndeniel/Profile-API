package com.profile.api.sociallink.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.sociallink.dto.SocialLinkRequestDto;
import com.profile.api.sociallink.dto.SocialLinkResponseDto;
import com.profile.api.sociallink.service.SocialLinkService;
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
@RequestMapping("/v1/social-links")
@Tag(name = "Social Links", description = "Social Links Management")
public class SocialLinkController {

    private final SocialLinkService socialLinkService;

    public SocialLinkController(SocialLinkService socialLinkService) {
        this.socialLinkService = socialLinkService;
    }

    @Operation(parameters = {
            @Parameter(name = "Idempotency-Key", in = ParameterIn.HEADER, description = "Unique key for idempotent request (max 64 chars)", required = true, schema = @Schema(maxLength = 64))
    })
    @PostMapping
    public ResponseEntity<SocialLinkResponseDto> createSocialLink(
            @Valid @RequestBody SocialLinkRequestDto requestDto) {
        SocialLinkResponseDto created = socialLinkService.createSocialLink(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<SocialLinkResponseDto>> getAllSocialLinks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID uploaderId,
            @RequestParam(required = false) String platform,
            @RequestParam(required = false) String search) {

        PaginatedResponseDto<SocialLinkResponseDto> result =
                socialLinkService.getSocialLinks(page, size, sortBy, sortDirection, id, uploaderId, platform, search);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SocialLinkResponseDto> updateSocialLink(
            @PathVariable UUID id,
            @Valid @RequestBody SocialLinkRequestDto requestDto) {
        SocialLinkResponseDto updated = socialLinkService.updateSocialLink(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSocialLink(@PathVariable UUID id) {
        socialLinkService.deleteSocialLink(id);
        return ResponseEntity.noContent().build();
    }
}
