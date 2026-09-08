package com.profile.api.sociallink.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.sociallink.dto.SocialLinkRequestDto;
import com.profile.api.sociallink.dto.SocialLinkResponseDto;
import com.profile.api.sociallink.mapper.SocialLinkMapper;
import com.profile.api.sociallink.model.SocialLink;
import com.profile.api.sociallink.repository.SocialLinkRepository;
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
public class SocialLinkService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(SocialLinkService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "uploaderId", "platform", "createdAt", "updatedAt"
    );

    private final SocialLinkRepository socialLinkRepository;

    public SocialLinkService(SocialLinkRepository socialLinkRepository) {
        this.socialLinkRepository = socialLinkRepository;
    }

    @Transactional
    public SocialLinkResponseDto createSocialLink(SocialLinkRequestDto requestDto) {
        if (requestDto.getUploaderId() == null) {
            throw new IllegalArgumentException("Uploader ID is required");
        }
        if (requestDto.getPlatform() == null) {
            throw new IllegalArgumentException("Platform is required");
        }
        if (requestDto.getPlatformUrl() == null || requestDto.getPlatformUrl().isBlank()) {
            throw new IllegalArgumentException("Platform URL is required");
        }
        SocialLink entity = SocialLinkMapper.toEntity(requestDto);
        SocialLink saved = socialLinkRepository.save(entity);
        log.info("Created social link id={}", saved.getId());
        return SocialLinkMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<SocialLinkResponseDto> getSocialLinks(
            int page, int size, String sortBy, String sortDirection,
            UUID id, UUID uploaderId, String platform, String search) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "createdAt";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<SocialLink> spec = buildSpec(id, uploaderId, platform, search);

        Page<SocialLink> result = socialLinkRepository.findAll(spec, pageable);

        List<SocialLinkResponseDto> content = result.getContent()
                .stream()
                .map(SocialLinkMapper::toResponseDto)
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
    public SocialLinkResponseDto updateSocialLink(UUID id, SocialLinkRequestDto requestDto) {
        SocialLink existing = findSocialLinkOrThrow(id);
        SocialLinkMapper.updateEntity(existing, requestDto);
        SocialLink saved = socialLinkRepository.save(existing);
        log.info("Updated social link id={}", id);
        return SocialLinkMapper.toResponseDto(saved);
    }

    @Transactional
    public void deleteSocialLink(UUID id) {
        SocialLink entity = findSocialLinkOrThrow(id);
        socialLinkRepository.delete(entity);
        log.info("Deleted social link id={}", id);
    }

    private SocialLink findSocialLinkOrThrow(UUID id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SocialLink", "id", id));
    }

    private Specification<SocialLink> buildSpec(UUID id, UUID uploaderId, String platform, String search) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (uploaderId != null) {
                predicates.add(cb.equal(root.get("uploaderId"), uploaderId));
            }

            if (platform != null && !platform.isEmpty()) {
                predicates.add(cb.equal(root.get("platform"), platform));
            }

            if (search != null && !search.isEmpty()) {
                String pattern = "%" + escapeSqlWildcard(search.toLowerCase()) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("platform")), pattern, '\\')
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String escapeSqlWildcard(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
