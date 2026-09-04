package com.profile.api.personalinformation.controller;

import com.profile.api.common.logging.LogConstants;
import com.profile.api.personalinformation.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationQueryDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.service.PersonalInformationService;
import jakarta.validation.Valid;
import org.slf4j.MDC;
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
        MDC.put(LogConstants.BOUNDED_CONTEXT, "PROFILE");
        try {
            PersonalInformationResponseDto created = personalInformationService.createPersonalInformation(requestDto);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } finally {
            MDC.remove(LogConstants.BOUNDED_CONTEXT);
        }
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<PersonalInformationResponseDto>> getAllPersonalInformation(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String middleName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String location) {
        MDC.put(LogConstants.BOUNDED_CONTEXT, "PROFILE");
        try {
            PersonalInformationQueryDto query = new PersonalInformationQueryDto();
            query.setPage(page);
            query.setSize(size);
            query.setSortBy(sortBy);
            query.setSortDirection(sortDirection);
            query.setSearch(search);
            query.setFirstName(firstName);
            query.setMiddleName(middleName);
            query.setLastName(lastName);
            query.setLocation(location);

            PaginatedResponseDto<PersonalInformationResponseDto> result =
                    personalInformationService.getPersonalInformationWithFilters(query);
            return ResponseEntity.ok(result);
        } finally {
            MDC.remove(LogConstants.BOUNDED_CONTEXT);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalInformationResponseDto> getPersonalInformationById(@PathVariable UUID id) {
        MDC.put(LogConstants.BOUNDED_CONTEXT, "PROFILE");
        try {
            PersonalInformationResponseDto dto = personalInformationService.getPersonalInformationById(id);
            return ResponseEntity.ok(dto);
        } finally {
            MDC.remove(LogConstants.BOUNDED_CONTEXT);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalInformationResponseDto> updatePersonalInformation(
            @PathVariable UUID id,
            @Valid @RequestBody PersonalInformationRequestDto requestDto) {
        MDC.put(LogConstants.BOUNDED_CONTEXT, "PROFILE");
        try {
            PersonalInformationResponseDto updated = personalInformationService.updatePersonalInformation(id, requestDto);
            return ResponseEntity.ok(updated);
        } finally {
            MDC.remove(LogConstants.BOUNDED_CONTEXT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalInformation(@PathVariable UUID id) {
        MDC.put(LogConstants.BOUNDED_CONTEXT, "PROFILE");
        try {
            personalInformationService.deletePersonalInformation(id);
            return ResponseEntity.noContent().build();
        } finally {
            MDC.remove(LogConstants.BOUNDED_CONTEXT);
        }
    }
}
