package com.profile.api.curriculumvitae.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.curriculumvitae.dto.CurriculumVitaeRequestDto;
import com.profile.api.curriculumvitae.dto.CurriculumVitaeResponseDto;
import com.profile.api.curriculumvitae.mapper.CurriculumVitaeMapper;
import com.profile.api.curriculumvitae.model.CurriculumVitae;
import com.profile.api.curriculumvitae.repository.CurriculumVitaeRepository;
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
public class CurriculumVitaeService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(CurriculumVitaeService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "uploaderId", "issued", "createdAt", "updatedAt"
    );

    private final CurriculumVitaeRepository curriculumVitaeRepository;

    public CurriculumVitaeService(CurriculumVitaeRepository curriculumVitaeRepository) {
        this.curriculumVitaeRepository = curriculumVitaeRepository;
    }

    @Transactional
    public CurriculumVitaeResponseDto createCurriculumVitae(CurriculumVitaeRequestDto requestDto) {
        CurriculumVitae entity = CurriculumVitaeMapper.toEntity(requestDto);
        CurriculumVitae saved = curriculumVitaeRepository.save(entity);
        log.info("Created curriculum vitae id={}", saved.getId());
        return CurriculumVitaeMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<CurriculumVitaeResponseDto> getCurriculumVitaes(
            int page, int size, String sortBy, String sortDirection,
            UUID id, UUID uploaderId) {

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
        Specification<CurriculumVitae> spec = buildSpec(id, uploaderId);

        Page<CurriculumVitae> result = curriculumVitaeRepository.findAll(spec, pageable);

        List<CurriculumVitaeResponseDto> content = result.getContent()
                .stream()
                .map(CurriculumVitaeMapper::toResponseDto)
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
    public CurriculumVitaeResponseDto updateCurriculumVitae(UUID id, CurriculumVitaeRequestDto requestDto) {
        CurriculumVitae existing = findCurriculumVitaeOrThrow(id);
        validateProvidedFields(requestDto);
        CurriculumVitaeMapper.updateEntity(existing, requestDto);
        CurriculumVitae saved = curriculumVitaeRepository.save(existing);
        log.info("Updated curriculum vitae id={}", id);
        return CurriculumVitaeMapper.toResponseDto(saved);
    }

    private void validateProvidedFields(CurriculumVitaeRequestDto requestDto) {
        if (requestDto.getBlobUrl() != null) {
            if (requestDto.getBlobUrl().isBlank()) {
                throw new IllegalArgumentException("Blob URL must not be blank");
            }
            try {
                java.net.URI.create(requestDto.getBlobUrl()).toURL();
            } catch (Exception e) {
                throw new IllegalArgumentException("Blob URL must be a valid URL");
            }
        }
        if (requestDto.getIssued() != null && requestDto.getIssued().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Issued date must not be in the future");
        }
    }

    @Transactional
    public void deleteCurriculumVitae(UUID id) {
        CurriculumVitae entity = findCurriculumVitaeOrThrow(id);
        curriculumVitaeRepository.delete(entity);
        log.info("Deleted curriculum vitae id={}", id);
    }

    private CurriculumVitae findCurriculumVitaeOrThrow(UUID id) {
        return curriculumVitaeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CurriculumVitae", "id", id));
    }

    private Specification<CurriculumVitae> buildSpec(UUID id, UUID uploaderId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (uploaderId != null) {
                predicates.add(cb.equal(root.get("uploaderId"), uploaderId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
