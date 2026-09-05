package com.profile.api.common.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PaginatedResponseDto<T> {

    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int currentPage;
    private final int size;

    public PaginatedResponseDto(List<T> content, long totalElements, int totalPages, int currentPage, int size) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.size = size;
    }
}
