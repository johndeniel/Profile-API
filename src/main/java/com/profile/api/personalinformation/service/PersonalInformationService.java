package com.profile.api.personalinformation.service;

import com.profile.api.exception.ResourceNotFoundException;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.model.PersonalInformation;
import com.profile.api.personalinformation.repository.PersonalInformationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonalInformationService {

    private final PersonalInformationRepository personalInformationRepository;

    public PersonalInformationService(PersonalInformationRepository personalInformationRepository) {
        this.personalInformationRepository = personalInformationRepository;
    }

    @Transactional
    public PersonalInformationResponseDto createPersonalInformation(PersonalInformationRequestDto requestDto) {
        PersonalInformation personalInformation = convertToEntity(requestDto);
        personalInformation.setId(null);
        personalInformation.setCreatedAt(null);
        personalInformation.setUpdatedAt(null);
        PersonalInformation saved = personalInformationRepository.save(personalInformation);
        return convertToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PersonalInformationResponseDto> getAllPersonalInformation() {
        return personalInformationRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonalInformationResponseDto getPersonalInformationById(UUID id) {
        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation", "id", id));
        return convertToResponseDto(personalInformation);
    }

    @Transactional
    public PersonalInformationResponseDto updatePersonalInformation(UUID id, PersonalInformationRequestDto requestDto) {
        PersonalInformation existing = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation", "id", id));
        
        existing.setFirstName(requestDto.getFirstName());
        existing.setMiddleName(requestDto.getMiddleName());
        existing.setLastName(requestDto.getLastName());
        existing.setHeadline(requestDto.getHeadline());
        existing.setProfileImageUrl(requestDto.getProfileImageUrl());
        existing.setEmailAddress(requestDto.getEmailAddress());
        existing.setPhoneNumber(requestDto.getPhoneNumber());
        existing.setLocation(requestDto.getLocation());
        
        PersonalInformation saved = personalInformationRepository.save(existing);
        return convertToResponseDto(saved);
    }

    @Transactional
    public void deletePersonalInformation(UUID id) {
        if (!personalInformationRepository.existsById(id)) {
            throw new ResourceNotFoundException("PersonalInformation", "id", id);
        }
        personalInformationRepository.deleteById(id);
    }

    private PersonalInformation convertToEntity(PersonalInformationRequestDto dto) {
        PersonalInformation entity = new PersonalInformation();
        entity.setFirstName(dto.getFirstName());
        entity.setMiddleName(dto.getMiddleName());
        entity.setLastName(dto.getLastName());
        entity.setHeadline(dto.getHeadline());
        entity.setProfileImageUrl(dto.getProfileImageUrl());
        entity.setEmailAddress(dto.getEmailAddress());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setLocation(dto.getLocation());
        return entity;
    }

    private PersonalInformationResponseDto convertToResponseDto(PersonalInformation entity) {
        PersonalInformationResponseDto dto = new PersonalInformationResponseDto();
        dto.setId(entity.getId());
        dto.setFirstName(entity.getFirstName());
        dto.setMiddleName(entity.getMiddleName());
        dto.setLastName(entity.getLastName());
        dto.setHeadline(entity.getHeadline());
        dto.setProfileImageUrl(entity.getProfileImageUrl());
        dto.setEmailAddress(entity.getEmailAddress());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setLocation(entity.getLocation());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
