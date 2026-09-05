package com.profile.api.personalinformation.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.logging.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.mapper.PersonalInformationMapper;
import com.profile.api.personalinformation.model.PersonalInformation;
import com.profile.api.personalinformation.repository.PersonalInformationRepository;
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
public class PersonalInformationService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(PersonalInformationService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "firstName", "middleName", "lastName", "headline",
            "emailAddress", "phoneNumber", "location", "createdAt", "updatedAt"
    );

    private final PersonalInformationRepository personalInformationRepository;

    public PersonalInformationService(PersonalInformationRepository personalInformationRepository) {
        this.personalInformationRepository = personalInformationRepository;
    }

    @Transactional
    public PersonalInformationResponseDto createPersonalInformation(PersonalInformationRequestDto requestDto) {
        PersonalInformation entity = PersonalInformationMapper.toEntity(requestDto);
        PersonalInformation saved = personalInformationRepository.save(entity);
        log.info("Created personal information id={}", saved.getId());
        return PersonalInformationMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<PersonalInformationResponseDto> getPersonalInformation(
            int page, int size, String sortBy, String sortDirection,
            UUID id, String search, String firstName, String lastName, String location) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "createdAt";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<PersonalInformation> spec = buildSpec(id, search, firstName, lastName, location);

        Page<PersonalInformation> result = personalInformationRepository.findAll(spec, pageable);

        List<PersonalInformationResponseDto> content = result.getContent()
                .stream()
                .map(PersonalInformationMapper::toResponseDto)
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
    public PersonalInformationResponseDto updatePersonalInformation(UUID id, PersonalInformationRequestDto requestDto) {
        PersonalInformation existing = findPersonalInformationOrThrow(id);
        PersonalInformationMapper.updateEntity(existing, requestDto);
        PersonalInformation saved = personalInformationRepository.save(existing);
        log.info("Updated personal information id={}", id);
        return PersonalInformationMapper.toResponseDto(saved);
    }

    @Transactional
    public void deletePersonalInformation(UUID id) {
        PersonalInformation entity = findPersonalInformationOrThrow(id);
        personalInformationRepository.delete(entity);
        log.info("Deleted personal information id={}", id);
    }

    private PersonalInformation findPersonalInformationOrThrow(UUID id) {
        return personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation", "id", id));
    }

    private Specification<PersonalInformation> buildSpec(UUID id, String search, String firstName, String lastName, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }

            if (search != null && !search.isEmpty()) {
                String pattern = "%" + escapeSqlWildcard(search.toLowerCase()) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern, '\\'),
                        cb.like(cb.lower(root.get("lastName")), pattern, '\\'),
                        cb.like(cb.lower(root.get("headline")), pattern, '\\'),
                        cb.like(cb.lower(root.get("location")), pattern, '\\')
                ));
            }
            addFilter(predicates, cb, root, "firstName", firstName);
            addFilter(predicates, cb, root, "lastName", lastName);
            addFilter(predicates, cb, root, "location", location);

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void addFilter(List<Predicate> predicates, CriteriaBuilder cb,
                           Root<PersonalInformation> root, String field, String value) {
        if (value != null && !value.isEmpty()) {
            String pattern = "%" + escapeSqlWildcard(value.toLowerCase()) + "%";
            predicates.add(cb.like(cb.lower(root.get(field)), pattern, '\\'));
        }
    }

    private String escapeSqlWildcard(String input) {
        return input.replace("%", "\\%").replace("_", "\\_");
    }
}
