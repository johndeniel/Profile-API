package com.profile.api.licensecertificate.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.licensecertificate.dto.LicenseCertificateRequestDto;
import com.profile.api.licensecertificate.dto.LicenseCertificateResponseDto;
import com.profile.api.licensecertificate.mapper.LicenseCertificateMapper;
import com.profile.api.licensecertificate.model.LicenseCertificate;
import com.profile.api.licensecertificate.repository.LicenseCertificateRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LicenseCertificateService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(LicenseCertificateService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "issuer", "issued", "level", "credentialId", "credentialUrl", "description", "blobUrl", "createdAt", "updatedAt"
    );

    private final LicenseCertificateRepository licenseCertificateRepository;

    public LicenseCertificateService(LicenseCertificateRepository licenseCertificateRepository) {
        this.licenseCertificateRepository = licenseCertificateRepository;
    }

    @Transactional
    public LicenseCertificateResponseDto createLicenseCertificate(LicenseCertificateRequestDto requestDto) {
        LicenseCertificate entity = LicenseCertificateMapper.toEntity(requestDto);
        LicenseCertificate saved = licenseCertificateRepository.save(entity);
        log.info("Created license certificate id={}", saved.getId());
        return LicenseCertificateMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<LicenseCertificateResponseDto> getLicenseCertificates(
            int page, int size, String sortBy, String sortDirection,
            UUID id, String search, String title, String issuer) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "createdAt";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<LicenseCertificate> spec = buildSpec(id, search, title, issuer);

        Page<LicenseCertificate> result = licenseCertificateRepository.findAll(spec, pageable);

        List<LicenseCertificateResponseDto> content = result.getContent()
                .stream()
                .map(LicenseCertificateMapper::toResponseDto)
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
    public LicenseCertificateResponseDto updateLicenseCertificate(UUID id, LicenseCertificateRequestDto requestDto) {
        LicenseCertificate existing = findLicenseCertificateOrThrow(id);
        LicenseCertificateMapper.updateEntity(existing, requestDto);
        LicenseCertificate saved = licenseCertificateRepository.save(existing);
        log.info("Updated license certificate id={}", id);
        return LicenseCertificateMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteLicenseCertificate(UUID id) {
        LicenseCertificate entity = findLicenseCertificateOrThrow(id);
        licenseCertificateRepository.delete(entity);
        log.info("Deleted license certificate id={}", id);
    }

    private LicenseCertificate findLicenseCertificateOrThrow(UUID id) {
        return licenseCertificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LicenseCertificate", "id", id));
    }

    private Specification<LicenseCertificate> buildSpec(UUID id, String search, String title, String issuer) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (search != null && !search.isEmpty()) {
                String pattern = "%" + escapeSqlWildcard(search.toLowerCase()) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern, '\\'),
                        cb.like(cb.lower(root.get("issuer")), pattern, '\\'),
                        cb.like(cb.lower(root.get("credentialId")), pattern, '\\')
                ));
            }
            addFilter(predicates, cb, root, "title", title);
            addFilter(predicates, cb, root, "issuer", issuer);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addFilter(List<Predicate> predicates, CriteriaBuilder cb,
                           Root<LicenseCertificate> root, String field, String value) {
        if (value != null && !value.isEmpty()) {
            String pattern = "%" + escapeSqlWildcard(value.toLowerCase()) + "%";
            predicates.add(cb.like(cb.lower(root.get(field)), pattern, '\\'));
        }
    }

    private String escapeSqlWildcard(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
