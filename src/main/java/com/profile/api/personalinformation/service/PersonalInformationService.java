package com.profile.api.personalinformation.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.logging.Log;
import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationRequestDto;
import com.profile.api.personalinformation.dto.PersonalInformationResponseDto;
import com.profile.api.personalinformation.dto.PersonalInformationSearchRequest;
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

    private static final Log log = Log.get(PersonalInformationService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "desc";
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
    public PaginatedResponseDto<PersonalInformationResponseDto> getAll(PersonalInformationSearchRequest request) {
        int page = Math.max(request.getPage(), 0);
        int size = Math.min(Math.max(request.getSize(), 1), MAX_PAGE_SIZE);
        String sortBy = ALLOWED_SORT_FIELDS.contains(request.getSortBy()) ? request.getSortBy() : DEFAULT_SORT_FIELD;
        String sortDirection = isValidSortDirection(request.getSortDirection()) ? request.getSortDirection().toLowerCase() : DEFAULT_SORT_DIRECTION;

        Sort sort = Sort.by(sortDirection, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<PersonalInformation> spec = buildSpec(request.getSearch(), request.getFirstName(), request.getLastName(), request.getLocation());

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

    @Transactional(readOnly = true)
    public PersonalInformationResponseDto getPersonalInformationById(UUID id) {
        PersonalInformation entity = findPersonalInformationOrThrow(id);
        return PersonalInformationMapper.toResponseDto(entity);
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

    private boolean isValidSortDirection(String sortDirection) {
        return sortDirection != null
                && (sortDirection.equalsIgnoreCase("asc") || sortDirection.equalsIgnoreCase("desc"));
    }

    private Specification<PersonalInformation> buildSpec(String search, String firstName, String lastName, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
