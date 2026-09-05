package com.profile.api.fileStore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class FileStoreRequestDto {

    @NotNull(message = "Uploader ID is required")
    private UUID uploaderId;

    @NotNull(message = "Blob URL is required")
    private String blobUrl;
}
