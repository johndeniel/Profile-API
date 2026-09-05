package com.profile.api.fileStore.repository;

import com.profile.api.fileStore.model.FileStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileStoreRepository extends JpaRepository<FileStore, UUID> {
}
