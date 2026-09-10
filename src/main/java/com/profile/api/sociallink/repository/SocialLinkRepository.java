package com.profile.api.sociallink.repository;

import com.profile.api.sociallink.model.SocialLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SocialLinkRepository extends JpaRepository<SocialLink, UUID>, JpaSpecificationExecutor<SocialLink> {
}
