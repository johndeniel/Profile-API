package com.profile.api.curriculumvitae.repository;

import com.profile.api.curriculumvitae.model.CurriculumVitae;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CurriculumVitaeRepository extends JpaRepository<CurriculumVitae, UUID>, JpaSpecificationExecutor<CurriculumVitae> {
}
