package com.profile.api.fileStore.service;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.common.exception.ResourceNotFoundException;
import com.profile.api.common.logging.Log;
import com.profile.api.common.storage.VercelBlobService;
import com.profile.api.fileStore.dto.FileStoreRequestDto;
import com.profile.api.fileStore.dto.FileStoreResponseDto;
import com.profile.api.fileStore.mapper.FileStoreMapper;
import com.profile.api.fileStore.model.FileStore;
import com.profile.api.fileStore.repository.FileStoreRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStoreService {

    private static final Log log = Log.get(FileStoreService.class);

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "uploaderId", "blobUrl", "createdAt", "updatedAt"
    );

    private final FileStoreRepository fileStoreRepository;
    private final VercelBlobService vercelBlobService;

    public FileStoreService(FileStoreRepository fileStoreRepository, VercelBlobService vercelBlobService) {
        this.fileStoreRepository = fileStoreRepository;
        this.vercelBlobService = vercelBlobService;
    }

    @Transactional
    public FileStoreResponseDto uploadFile(MultipartFile file, UUID uploaderId) {
        try {
            String pathname = "uploads/" + file.getOriginalFilename();
            String blobUrl = vercelBlobService.uploadWithRandomSuffix(pathname, file.getBytes(), file.getContentType());

            FileStore entity = new FileStore();
            entity.setUploaderId(uploaderId);
            entity.setBlobUrl(blobUrl);
            FileStore saved = fileStoreRepository.save(entity);

            log.info("Uploaded file id={} -> {}", saved.getId(), blobUrl);
            return FileStoreMapper.toResponseDto(saved);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + file.getOriginalFilename(), e);
        }
    }

    @Transactional
    public List<FileStoreResponseDto> uploadFiles(List<MultipartFile> files, UUID uploaderId) {
        List<FileStoreResponseDto> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadFile(file, uploaderId));
        }
        return results;
    }

    @Transactional
    public List<FileStoreResponseDto> createFiles(List<FileStoreRequestDto> requestDtos) {
        List<FileStoreResponseDto> results = new ArrayList<>();
        for (FileStoreRequestDto dto : requestDtos) {
            FileStore entity = FileStoreMapper.toEntity(dto);
            FileStore saved = fileStoreRepository.save(entity);
            log.info("Created file store id={}", saved.getId());
            results.add(FileStoreMapper.toResponseDto(saved));
        }
        return results;
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

        List<FileStore> result;

        if (id != null) {
            FileStore entity = fileStoreRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("FileStore", "id", id));
            result = List.of(entity);
        } else if (uploaderId != null) {
            result = fileStoreRepository.findAll(pageable).stream()
                    .filter(f -> f.getUploaderId().equals(uploaderId))
                    .collect(Collectors.toList());
        } else {
            result = fileStoreRepository.findAll(pageable).getContent();
        }

        List<FileStoreResponseDto> content = result.stream()
                .map(FileStoreMapper::toResponseDto)
                .collect(Collectors.toList());

        return new PaginatedResponseDto<>(
                content,
                (long) content.size(),
                1,
                page,
                size
        );
    }

    @Transactional
    public void deleteFiles(List<UUID> ids) {
        for (UUID id : ids) {
            FileStore entity = fileStoreRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("FileStore", "id", id));

            String blobUrl = entity.getBlobUrl();
            if (blobUrl != null && !blobUrl.isEmpty()) {
                vercelBlobService.delete(blobUrl);
            }

            fileStoreRepository.delete(entity);
            log.info("Deleted file store id={}", id);
        }
    }
}
