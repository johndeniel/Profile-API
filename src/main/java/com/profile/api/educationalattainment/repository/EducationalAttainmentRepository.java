package com.profile.api.educationalattainment.repository;

import com.profile.api.educationalattainment.model.EducationalAttainment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface EducationalAttainmentRepository extends JpaRepository<EducationalAttainment, UUID>, JpaSpecificationExecutor<EducationalAttainment> {
}
