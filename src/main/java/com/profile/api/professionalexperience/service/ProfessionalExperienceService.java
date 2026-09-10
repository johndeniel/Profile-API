package com.profile.api.professionalexperience.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.professionalexperience.dto.ProfessionalExperienceRequestDto;
import com.profile.api.professionalexperience.dto.ProfessionalExperienceResponseDto;
import com.profile.api.professionalexperience.mapper.ProfessionalExperienceMapper;
import com.profile.api.professionalexperience.model.ProfessionalExperience;
import com.profile.api.professionalexperience.repository.ProfessionalExperienceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfessionalExperienceService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(ProfessionalExperienceService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "uploaderId", "title", "company", "type", "location", "startDate", "endDate", "createdAt", "updatedAt"
    );

    private final ProfessionalExperienceRepository professionalExperienceRepository;

    public ProfessionalExperienceService(ProfessionalExperienceRepository professionalExperienceRepository) {
        this.professionalExperienceRepository = professionalExperienceRepository;
    }

    @Transactional
    public ProfessionalExperienceResponseDto createProfessionalExperience(ProfessionalExperienceRequestDto requestDto) {
        validateProvidedFields(requestDto);
        ProfessionalExperience entity = ProfessionalExperienceMapper.toEntity(requestDto);
        ProfessionalExperience saved = professionalExperienceRepository.save(entity);
        log.info("Created professional experience id={}", saved.getId());
        return ProfessionalExperienceMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<ProfessionalExperienceResponseDto> getProfessionalExperiences(
            int page, int size, String sortBy, String sortDirection,
            UUID id, UUID uploaderId, String search, String title, String company, String type, String location) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "createdAt";

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            direction = Sort.Direction.DESC;
        }
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<ProfessionalExperience> spec = buildSpec(id, uploaderId, search, title, company, type, location);

        Page<ProfessionalExperience> result = professionalExperienceRepository.findAll(spec, pageable);

        List<ProfessionalExperienceResponseDto> content = result.getContent()
                .stream()
                .map(ProfessionalExperienceMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PaginatedResponseDto<>(
                content,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Transactional
    public ProfessionalExperienceResponseDto updateProfessionalExperience(UUID id, ProfessionalExperienceRequestDto requestDto) {
        ProfessionalExperience existing = findProfessionalExperienceOrThrow(id);
        validateProvidedFields(requestDto);
        ProfessionalExperienceMapper.updateEntity(existing, requestDto);
        ProfessionalExperience saved = professionalExperienceRepository.save(existing);
        log.info("Updated professional experience id={}", id);
        return ProfessionalExperienceMapper.toResponseDto(saved);
    }

    private void validateProvidedFields(ProfessionalExperienceRequestDto requestDto) {
        if (requestDto.getTitle() != null) {
            if (requestDto.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title must not be blank");
            }
            if (requestDto.getTitle().length() > 255) {
                throw new IllegalArgumentException("Title must not exceed 255 characters");
            }
        }
        if (requestDto.getCompany() != null) {
            if (requestDto.getCompany().isBlank()) {
                throw new IllegalArgumentException("Company must not be blank");
            }
            if (requestDto.getCompany().length() > 255) {
                throw new IllegalArgumentException("Company must not exceed 255 characters");
            }
        }
        if (requestDto.getType() != null && !requestDto.getType().matches("^(FULL_TIME|PART_TIME|CONTRACT|INTERNSHIP|FREELANCE|SELF_EMPLOYED)$")) {
            throw new IllegalArgumentException("Type must be one of: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, FREELANCE, SELF_EMPLOYED");
        }
        if (requestDto.getLocation() != null && requestDto.getLocation().length() > 255) {
            throw new IllegalArgumentException("Location must not exceed 255 characters");
        }
        if (requestDto.getStartDate() != null && requestDto.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date must not be in the future");
        }
        if (requestDto.getEndDate() != null && requestDto.getEndDate().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("End date must not be in the future");
        }
        if (requestDto.getStartDate() != null && requestDto.getEndDate() != null
                && requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
    }

    @Transactional
    public void deleteProfessionalExperience(UUID id) {
        ProfessionalExperience entity = findProfessionalExperienceOrThrow(id);
        professionalExperienceRepository.delete(entity);
        log.info("Deleted professional experience id={}", id);
    }

    private ProfessionalExperience findProfessionalExperienceOrThrow(UUID id) {
        return professionalExperienceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProfessionalExperience", "id", id));
    }

    private Specification<ProfessionalExperience> buildSpec(UUID id, UUID uploaderId, String search, String title, String company, String type, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (uploaderId != null) {
                predicates.add(cb.equal(root.get("uploaderId"), uploaderId));
            }

            if (search != null && !search.isEmpty()) {
                String pattern = "%" + escapeSqlWildcard(search.toLowerCase()) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern, '\\'),
                        cb.like(cb.lower(root.get("company")), pattern, '\\'),
                        cb.like(cb.lower(root.get("location")), pattern, '\\')
                ));
            }
            addLikeFilter(predicates, cb, root, "title", title);
            addLikeFilter(predicates, cb, root, "company", company);
            addEqualsFilter(predicates, cb, root, "type", type);
            addLikeFilter(predicates, cb, root, "location", location);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addLikeFilter(List<Predicate> predicates, CriteriaBuilder cb,
                               Root<ProfessionalExperience> root, String field, String value) {
        if (value != null && !value.isEmpty()) {
            String pattern = "%" + escapeSqlWildcard(value.toLowerCase()) + "%";
            predicates.add(cb.like(cb.lower(root.get(field)), pattern, '\\'));
        }
    }

    private void addEqualsFilter(List<Predicate> predicates, CriteriaBuilder cb,
                                 Root<ProfessionalExperience> root, String field, String value) {
        if (value != null && !value.isEmpty()) {
            predicates.add(cb.equal(cb.lower(root.get(field)), value.toLowerCase()));
        }
    }

    private String escapeSqlWildcard(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
