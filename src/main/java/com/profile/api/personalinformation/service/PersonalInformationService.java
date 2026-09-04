package com.profile.api.personalinformation.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.logging.BoundedContextTemplates;
import com.profile.api.personalinformation.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationQueryDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.model.PersonalInformation;
import com.profile.api.personalinformation.repository.PersonalInformationRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonalInformationService {

    private static final Logger log = LoggerFactory.getLogger(PersonalInformationService.class);

    private final PersonalInformationRepository personalInformationRepository;

    public PersonalInformationService(PersonalInformationRepository personalInformationRepository) {
        this.personalInformationRepository = personalInformationRepository;
    }

    @Transactional
    public PersonalInformationResponseDto createPersonalInformation(PersonalInformationRequestDto requestDto) {
        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "createPersonalInformation", "Processing",
                String.format("firstName=%s, lastName=%s", requestDto.getFirstName(), requestDto.getLastName()),
                "pending", "0ms");

        PersonalInformation personalInformation = convertToEntity(requestDto);
        personalInformation.setId(null);
        personalInformation.setCreatedAt(null);
        personalInformation.setUpdatedAt(null);
        PersonalInformation saved = personalInformationRepository.save(personalInformation);

        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "createPersonalInformation", "Completed",
                String.format("firstName=%s, lastName=%s", requestDto.getFirstName(), requestDto.getLastName()),
                String.format("id=%s", saved.getId()), "0ms");

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
    public PaginatedResponseDto<PersonalInformationResponseDto> getPersonalInformationWithFilters(
            PersonalInformationQueryDto query) {
        List<String> allowedSortFields = List.of(
                "id", "firstName", "middleName", "lastName", "headline",
                "emailAddress", "phoneNumber", "location", "createdAt", "updatedAt"
        );

        if (!allowedSortFields.contains(query.getSortBy())) {
            log.warn("Invalid sort field: {}. Using default: createdAt", query.getSortBy());
            query.setSortBy("createdAt");
        }

        if (!query.getSortDirection().equalsIgnoreCase("asc") && !query.getSortDirection().equalsIgnoreCase("desc")) {
            log.warn("Invalid sort direction: {}. Using default: desc", query.getSortDirection());
            query.setSortDirection("desc");
        }

        if (query.getPage() < 0) {
            log.warn("Invalid page: {}. Using default: 0", query.getPage());
            query.setPage(0);
        }

        if (query.getSize() < 1) {
            log.warn("Invalid size: {}. Using default: 10", query.getSize());
            query.setSize(10);
        } else if (query.getSize() > 100) {
            log.warn("Invalid size: {}. Using max: 100", query.getSize());
            query.setSize(100);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(query.getSortDirection()), query.getSortBy());
        Pageable pageable = PageRequest.of(query.getPage(), query.getSize(), sort);

        Specification<PersonalInformation> spec = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (query.getSearch() != null && !query.getSearch().isEmpty()) {
                String searchPattern = "%" + query.getSearch().toLowerCase() + "%";
                Predicate searchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("headline")), searchPattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), searchPattern)
                );
                predicates.add(searchPredicate);
            }

            if (query.getFirstName() != null && !query.getFirstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("firstName")),
                        "%" + query.getFirstName().toLowerCase() + "%"
                ));
            }

            if (query.getMiddleName() != null && !query.getMiddleName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("middleName")),
                        "%" + query.getMiddleName().toLowerCase() + "%"
                ));
            }

            if (query.getLastName() != null && !query.getLastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("lastName")),
                        "%" + query.getLastName().toLowerCase() + "%"
                ));
            }

            if (query.getLocation() != null && !query.getLocation().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("location")),
                        "%" + query.getLocation().toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<PersonalInformation> page = personalInformationRepository.findAll(spec, pageable);

        List<PersonalInformationResponseDto> content = page.getContent()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());

        return new PaginatedResponseDto<>(
                content,
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize()
        );
    }

    @Transactional(readOnly = true)
    public PersonalInformationResponseDto getPersonalInformationById(UUID id) {
        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "getPersonalInformationById", "Processing",
                String.format("id=%s", id), "pending", "0ms");

        PersonalInformation personalInformation = personalInformationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Personal information not found with id: {}", id);
                    return new ResourceNotFoundException("PersonalInformation", "id", id);
                });

        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "getPersonalInformationById", "Completed",
                String.format("id=%s", id),
                String.format("firstName=%s, lastName=%s", personalInformation.getFirstName(), personalInformation.getLastName()),
                "0ms");

        return convertToResponseDto(personalInformation);
    }

    @Transactional
    public PersonalInformationResponseDto updatePersonalInformation(UUID id, PersonalInformationRequestDto requestDto) {
        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "updatePersonalInformation", "Processing",
                String.format("id=%s", id), "pending", "0ms");

        PersonalInformation existing = personalInformationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Personal information not found with id: {} for update", id);
                    return new ResourceNotFoundException("PersonalInformation", "id", id);
                });
        
        existing.setFirstName(requestDto.getFirstName());
        existing.setMiddleName(requestDto.getMiddleName());
        existing.setLastName(requestDto.getLastName());
        existing.setHeadline(requestDto.getHeadline());
        existing.setProfileImageUrl(requestDto.getProfileImageUrl());
        existing.setEmailAddress(requestDto.getEmailAddress());
        existing.setPhoneNumber(requestDto.getPhoneNumber());
        existing.setLocation(requestDto.getLocation());
        
        PersonalInformation saved = personalInformationRepository.save(existing);

        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "updatePersonalInformation", "Completed",
                String.format("id=%s", id),
                String.format("firstName=%s, lastName=%s", saved.getFirstName(), saved.getLastName()),
                "0ms");

        return convertToResponseDto(saved);
    }

    @Transactional
    public void deletePersonalInformation(UUID id) {
        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "deletePersonalInformation", "Processing",
                String.format("id=%s", id), "pending", "0ms");

        if (!personalInformationRepository.existsById(id)) {
            log.warn("Personal information not found with id: {} for deletion", id);
            throw new ResourceNotFoundException("PersonalInformation", "id", id);
        }
        personalInformationRepository.deleteById(id);

        log.info(BoundedContextTemplates.SERVICE_LOG_TEMPLATE,
                "deletePersonalInformation", "Completed",
                String.format("id=%s", id), "deleted", "0ms");
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
