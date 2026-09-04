package com.profile.api.personalinformation.repository;

import com.profile.api.personalinformation.model.PersonalInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonalInformationRepository extends JpaRepository<PersonalInformation, UUID> {
}
