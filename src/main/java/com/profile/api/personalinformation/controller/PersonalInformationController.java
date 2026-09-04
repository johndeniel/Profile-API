package com.profile.api.personalinformation.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.service.PersonalInformationService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/personal-information")
public class PersonalInformationController {

    private final PersonalInformationService personalInformationService;

    public PersonalInformationController(PersonalInformationService personalInformationService) {
        this.personalInformationService = personalInformationService;
    }

    @PostMapping
    public ResponseEntity<PersonalInformationResponseDto> createPersonalInformation(
            @Valid @RequestBody PersonalInformationRequestDto requestDto) {
        PersonalInformationResponseDto created = personalInformationService.createPersonalInformation(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<PersonalInformationResponseDto>> getAllPersonalInformation(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Field to sort by", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,

            @Parameter(description = "Sort direction (asc/desc)", example = "desc")
            @RequestParam(defaultValue = "desc") String sortDirection,

            @Parameter(description = "Filter by ID")
            @RequestParam(required = false) UUID id,

            @Parameter(description = "Search keyword across multiple fields")
            @RequestParam(required = false) String search,

            @Parameter(description = "Filter by first name")
            @RequestParam(required = false) String firstName,

            @Parameter(description = "Filter by last name")
            @RequestParam(required = false) String lastName,

            @Parameter(description = "Filter by location")
            @RequestParam(required = false) String location) {

        PaginatedResponseDto<PersonalInformationResponseDto> result =
                personalInformationService.getAll(page, size, sortBy, sortDirection, id, search, firstName, lastName, location);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalInformationResponseDto> updatePersonalInformation(
            @PathVariable UUID id,
            @Valid @RequestBody PersonalInformationRequestDto requestDto) {
        PersonalInformationResponseDto updated = personalInformationService.updatePersonalInformation(id, requestDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalInformation(@PathVariable UUID id) {
        personalInformationService.deletePersonalInformation(id);
        return ResponseEntity.noContent().build();
    }
}
