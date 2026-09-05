package com.profile.api.personalinformation.repository;

import com.profile.api.personalinformation.model.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, UUID>, JpaSpecificationExecutor<PersonalInformation> {
}
