package com.profile.api.fileStore.repository;

import com.profile.api.fileStore.model.FileStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileStoreRepository extends JpaRepository<FileStore, UUID> {

    Page<FileStore> findByUploaderId(UUID uploaderId, Pageable pageable);
}
