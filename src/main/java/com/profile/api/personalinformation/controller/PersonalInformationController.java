package com.profile.api.personalinformation.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.service.PersonalInformationService;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
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
