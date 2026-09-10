package com.profile.api.professionalexperience.repository;

import com.profile.api.professionalexperience.model.ProfessionalExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProfessionalExperienceRepository extends JpaRepository<ProfessionalExperience, UUID>, JpaSpecificationExecutor<ProfessionalExperience> {
}
