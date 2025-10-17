package com.project.mealplan.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

public record PagePayLoad<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
    public static <T> PagePayLoad<T> of(Page<T> page) {
        return new PagePayLoad<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }
}
