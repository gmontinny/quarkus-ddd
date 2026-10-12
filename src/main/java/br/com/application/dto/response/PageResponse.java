package br.com.application.dto.response;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        List<Link> links
) {
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long total, List<Link> links) {
        int totalPages = (int) Math.ceil((double) total / size);
        return new PageResponse<>(content, page, size, total, totalPages, links);
    }
}
