package com.profile.api.educationalattainment.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.educationalattainment.dto.EducationalAttainmentRequestDto;
import com.profile.api.educationalattainment.dto.EducationalAttainmentResponseDto;
import com.profile.api.educationalattainment.mapper.EducationalAttainmentMapper;
import com.profile.api.educationalattainment.model.EducationalAttainment;
import com.profile.api.educationalattainment.repository.EducationalAttainmentRepository;
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
public class EducationalAttainmentService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(EducationalAttainmentService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "uploaderId", "institution", "startDate", "endDate", "award", "degree", "field", "createdAt", "updatedAt"
    );

    private final EducationalAttainmentRepository educationalAttainmentRepository;

    public EducationalAttainmentService(EducationalAttainmentRepository educationalAttainmentRepository) {
        this.educationalAttainmentRepository = educationalAttainmentRepository;
    }

    @Transactional
    public EducationalAttainmentResponseDto createEducationalAttainment(EducationalAttainmentRequestDto requestDto) {
        validateProvidedFields(requestDto);
        EducationalAttainment entity = EducationalAttainmentMapper.toEntity(requestDto);
        EducationalAttainment saved = educationalAttainmentRepository.save(entity);
        log.info("Created educational attainment id={}", saved.getId());
        return EducationalAttainmentMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<EducationalAttainmentResponseDto> getEducationalAttainments(
            int page, int size, String sortBy, String sortDirection,
            UUID id, UUID uploaderId, String search, String institution, String degree, String field, String award) {

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
        Specification<EducationalAttainment> spec = buildSpec(id, uploaderId, search, institution, degree, field, award);

        Page<EducationalAttainment> result = educationalAttainmentRepository.findAll(spec, pageable);

        List<EducationalAttainmentResponseDto> content = result.getContent()
                .stream()
                .map(EducationalAttainmentMapper::toResponseDto)
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
    public EducationalAttainmentResponseDto updateEducationalAttainment(UUID id, EducationalAttainmentRequestDto requestDto) {
        EducationalAttainment existing = findEducationalAttainmentOrThrow(id);
        validateProvidedFields(requestDto);
        EducationalAttainmentMapper.updateEntity(existing, requestDto);
        EducationalAttainment saved = educationalAttainmentRepository.save(existing);
        log.info("Updated educational attainment id={}", id);
        return EducationalAttainmentMapper.toResponseDto(saved);
    }

    private void validateProvidedFields(EducationalAttainmentRequestDto requestDto) {
        if (requestDto.getInstitution() != null) {
            if (requestDto.getInstitution().isBlank()) {
                throw new IllegalArgumentException("Institution must not be blank");
            }
            if (requestDto.getInstitution().length() > 255) {
                throw new IllegalArgumentException("Institution must not exceed 255 characters");
            }
        }
        if (requestDto.getAward() != null && requestDto.getAward().length() > 255) {
            throw new IllegalArgumentException("Award must not exceed 255 characters");
        }
        if (requestDto.getDegree() != null && requestDto.getDegree().length() > 255) {
            throw new IllegalArgumentException("Degree must not exceed 255 characters");
        }
        if (requestDto.getField() != null && requestDto.getField().length() > 255) {
            throw new IllegalArgumentException("Field must not exceed 255 characters");
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
    public void deleteEducationalAttainment(UUID id) {
        EducationalAttainment entity = findEducationalAttainmentOrThrow(id);
        educationalAttainmentRepository.delete(entity);
        log.info("Deleted educational attainment id={}", id);
    }

    private EducationalAttainment findEducationalAttainmentOrThrow(UUID id) {
        return educationalAttainmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EducationalAttainment", "id", id));
    }

    private Specification<EducationalAttainment> buildSpec(UUID id, UUID uploaderId, String search, String institution, String degree, String field, String award) {
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
                        cb.like(cb.lower(root.get("institution")), pattern, '\\'),
                        cb.like(cb.lower(root.get("degree")), pattern, '\\'),
                        cb.like(cb.lower(root.get("field")), pattern, '\\'),
                        cb.like(cb.lower(root.get("award")), pattern, '\\')
                ));
            }
            addLikeFilter(predicates, cb, root, "institution", institution);
            addLikeFilter(predicates, cb, root, "degree", degree);
            addLikeFilter(predicates, cb, root, "field", field);
            addLikeFilter(predicates, cb, root, "award", award);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addLikeFilter(List<Predicate> predicates, CriteriaBuilder cb,
                               Root<EducationalAttainment> root, String field, String value) {
        if (value != null && !value.isEmpty()) {
            String pattern = "%" + escapeSqlWildcard(value.toLowerCase()) + "%";
            predicates.add(cb.like(cb.lower(root.get(field)), pattern, '\\'));
        }
    }

    private String escapeSqlWildcard(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
