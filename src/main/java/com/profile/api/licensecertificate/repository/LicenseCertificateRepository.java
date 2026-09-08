package com.profile.api.licensecertificate.repository;

import com.profile.api.licensecertificate.model.LicenseCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface LicenseCertificateRepository extends JpaRepository<LicenseCertificate, UUID>, JpaSpecificationExecutor<LicenseCertificate> {
}
