package com.profile.api.fileStore.service;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.config.CentralizedLoggingFilter;
import org.slf4j.Logger;
import com.profile.api.common.config.VercelBlobService;
import com.profile.api.fileStore.dto.FileStoreResponseDto;
import com.profile.api.fileStore.mapper.FileStoreMapper;
import com.profile.api.fileStore.model.FileStore;
import com.profile.api.fileStore.repository.FileStoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStoreService {

    private static final Logger log = CentralizedLoggingFilter.getLogger(FileStoreService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_FILES_PER_UPLOAD = 20;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "uploaderId", "blobUrl", "createdAt", "updatedAt"
    );

    private final FileStoreRepository fileStoreRepository;
    private final VercelBlobService vercelBlobService;
    private final TransactionTemplate transactionTemplate;
    private final ImageProcessor imageProcessor;

    public FileStoreService(FileStoreRepository fileStoreRepository, VercelBlobService vercelBlobService,
                            TransactionTemplate transactionTemplate, ImageProcessor imageProcessor) {
        this.fileStoreRepository = fileStoreRepository;
        this.vercelBlobService = vercelBlobService;
        this.transactionTemplate = transactionTemplate;
        this.imageProcessor = imageProcessor;
    }

    public List<FileStoreResponseDto> uploadFiles(List<MultipartFile> files, UUID uploaderId) {
        if (files.size() > MAX_FILES_PER_UPLOAD) {
            throw new IllegalArgumentException(
                    "Too many files. Maximum is " + MAX_FILES_PER_UPLOAD + ", received " + files.size());
        }
        List<FileStoreResponseDto> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadFileInTransaction(file, uploaderId));
        }
        return results;
    }

    private FileStoreResponseDto uploadFileInTransaction(MultipartFile file, UUID uploaderId) {
        return transactionTemplate.execute(status -> uploadFile(file, uploaderId));
    }

    private FileStoreResponseDto uploadFile(MultipartFile file, UUID uploaderId) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "file-" + UUID.randomUUID().toString().substring(0, 8);
        } else {
            originalFilename = sanitizeFilename(originalFilename);
        }

        ImageProcessor.ProcessedImage processed;
        try {
            processed = imageProcessor.process(file.getBytes(), originalFilename);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + originalFilename, e);
        }

        String pathname = "uploads/" + originalFilename;
        String blobUrl = vercelBlobService.uploadWithRandomSuffix(pathname, processed.data(), processed.contentType());

        FileStore entity = new FileStore();
        entity.setUploaderId(uploaderId);
        entity.setBlobUrl(blobUrl);
        FileStore saved = fileStoreRepository.save(entity);

        log.info("Uploaded file id={} -> {} ({}x{}, {} bytes)", saved.getId(), blobUrl,
                processed.width(), processed.height(), processed.data().length);
        return FileStoreMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public PaginatedResponseDto<FileStoreResponseDto> getFiles(
            int page, int size, String sortBy, String sortDirection,
            UUID id, UUID uploaderId) {

        page = Math.max(page, 0);
        size = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) sortBy = "createdAt";

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FileStore> pageResult;

        if (id != null) {
            FileStore entity = fileStoreRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("FileStore", "id", id));
            pageResult = new org.springframework.data.domain.PageImpl<>(
                    List.of(entity), pageable, 1);
        } else if (uploaderId != null) {
            pageResult = fileStoreRepository.findByUploaderId(uploaderId, pageable);
        } else {
            pageResult = fileStoreRepository.findAll(pageable);
        }

        List<FileStoreResponseDto> content = pageResult.getContent().stream()
                .map(FileStoreMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PaginatedResponseDto<>(
                content,
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                page,
                size
        );
    }

    @Transactional
    public void deleteFiles(List<UUID> ids) {
        List<UUID> failedDeletes = new ArrayList<>();

        for (UUID id : ids) {
            FileStore entity = fileStoreRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("FileStore", "id", id));

            String blobUrl = entity.getBlobUrl();
            if (blobUrl != null && !blobUrl.isEmpty()) {
                boolean deleted = vercelBlobService.delete(blobUrl);
                if (!deleted) {
                    log.warn("Failed to delete blob for file store id={}: {}", id, blobUrl);
                    failedDeletes.add(id);
                    continue;
                }
            }

            fileStoreRepository.delete(entity);
            log.info("Deleted file store id={}", id);
        }

        if (!failedDeletes.isEmpty()) {
            log.warn("Skipped DB deletion for {} files due to blob delete failure: {}", failedDeletes.size(), failedDeletes);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9._\\-]", "_")
                .replaceAll("\\.+", ".")
                .replaceAll("^\\.", "")
                .replaceAll("\\.$", "");
    }
}
