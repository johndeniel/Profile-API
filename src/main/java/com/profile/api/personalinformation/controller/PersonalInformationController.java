package com.profile.api.personalinformation.controller;

import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.service.PersonalInformationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/personal-information")
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
    public ResponseEntity<List<PersonalInformationResponseDto>> getAllPersonalInformation() {
        List<PersonalInformationResponseDto> list = personalInformationService.getAllPersonalInformation();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonalInformationResponseDto> getPersonalInformationById(@PathVariable UUID id) {
        PersonalInformationResponseDto dto = personalInformationService.getPersonalInformationById(id);
        if (dto == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonalInformationResponseDto> updatePersonalInformation(
            @PathVariable UUID id,
            @Valid @RequestBody PersonalInformationRequestDto requestDto) {
        PersonalInformationResponseDto updated = personalInformationService.updatePersonalInformation(id, requestDto);
        if (updated == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePersonalInformation(@PathVariable UUID id) {
        boolean deleted = personalInformationService.deletePersonalInformation(id);
        if (!deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.noContent().build();
    }
}
