package com.profile.api.personalinformation.service;

import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.logging.Log;
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
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PersonalInformationService {

    private static final Log log = Log.get(PersonalInformationService.class);

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
    public PaginatedResponseDto<PersonalInformationResponseDto> getAll(
            int page, int size, String sortBy, String sortDirection,
            String search, String firstName, String lastName, String location) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<PersonalInformation> spec = buildSpec(search, firstName, lastName, location);

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
        PersonalInformation entity = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation", "id", id));
        return PersonalInformationMapper.toResponseDto(entity);
    }

    @Transactional
    public PersonalInformationResponseDto updatePersonalInformation(UUID id, PersonalInformationRequestDto requestDto) {
        PersonalInformation existing = personalInformationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PersonalInformation", "id", id));

        PersonalInformationMapper.updateEntity(existing, requestDto);

        PersonalInformation saved = personalInformationRepository.save(existing);
        log.info("Updated personal information id={}", id);
        return PersonalInformationMapper.toResponseDto(saved);
    }

    @Transactional
    public void deletePersonalInformation(UUID id) {
        if (!personalInformationRepository.existsById(id)) {
            throw new ResourceNotFoundException("PersonalInformation", "id", id);
        }
        personalInformationRepository.deleteById(id);
        log.info("Deleted personal information id={}", id);
    }

    private Specification<PersonalInformation> buildSpec(String search, String firstName, String lastName, String location) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isEmpty()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("headline")), pattern),
                        cb.like(cb.lower(root.get("location")), pattern)
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
            predicates.add(cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%"));
        }
    }
}
