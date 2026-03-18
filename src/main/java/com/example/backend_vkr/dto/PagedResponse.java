package com.example.backend_vkr.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(
        description = "Ответ с пагинацией"
)
public record PagedResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages, boolean last) {
    public PagedResponse(@Schema(description = "Содержимое страницы") List<T> content, @Schema(description = "Номер текущей страницы (начиная с 0)") int pageNumber, @Schema(description = "Размер страницы") int pageSize, @Schema(description = "Общее количество элементов") long totalElements, @Schema(description = "Общее количество страниц") int totalPages, @Schema(description = "Является ли страница последней") boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    @Schema(
            description = "Содержимое страницы"
    )
    public List<T> content() {
        return this.content;
    }

    @Schema(
            description = "Номер текущей страницы (начиная с 0)"
    )
    public int pageNumber() {
        return this.pageNumber;
    }

    @Schema(
            description = "Размер страницы"
    )
    public int pageSize() {
        return this.pageSize;
    }

    @Schema(
            description = "Общее количество элементов"
    )
    public long totalElements() {
        return this.totalElements;
    }

    @Schema(
            description = "Общее количество страниц"
    )
    public int totalPages() {
        return this.totalPages;
    }

    @Schema(
            description = "Является ли страница последней"
    )
    public boolean last() {
        return this.last;
    }
}
