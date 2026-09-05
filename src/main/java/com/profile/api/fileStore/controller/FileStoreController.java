package com.profile.api.fileStore.controller;

import com.profile.api.common.dto.PaginatedResponseDto;
import com.profile.api.fileStore.dto.FileStoreResponseDto;
import com.profile.api.fileStore.service.FileStoreService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/file-store")
@Tag(name = "File Store", description = "File upload and management")
public class FileStoreController {

    private final FileStoreService fileStoreService;

    public FileStoreController(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileStoreResponseDto>> uploadFiles(
            @Parameter(description = "File(s) to upload") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "Uploader ID") @RequestParam("uploaderId") UUID uploaderId) {
        List<FileStoreResponseDto> created = fileStoreService.uploadFiles(files, uploaderId);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponseDto<FileStoreResponseDto>> getFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) UUID uploaderId) {

        PaginatedResponseDto<FileStoreResponseDto> result =
                fileStoreService.getFiles(page, size, sortBy, sortDirection, id, uploaderId);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFiles(
            @RequestParam List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        fileStoreService.deleteFiles(ids);
        return ResponseEntity.noContent().build();
    }
}
