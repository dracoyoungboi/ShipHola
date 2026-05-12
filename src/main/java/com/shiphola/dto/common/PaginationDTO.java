package com.shiphola.dto.common;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * PaginationDTO - Common DTO cho phân trang
 * Dùng cho cả Request và Response
 */
public class PaginationDTO {

    @Min(value = 0, message = "Page phải >= 0")
    private int page = 0;

    @Min(value = 1, message = "Size phải >= 1")
    @Max(value = 100, message = "Size phải <= 100")
    private int size = 10;

    private String sort = "createdAt";

    private String direction = "desc";

    private long totalElements;

    private int totalPages;

    public PaginationDTO() {
    }

    public PaginationDTO(int page, int size, String sort, String direction) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.direction = direction;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
    }

    public int getTotalPages() { return totalPages; }

    public int getOffset() {
        return page * size;
    }

    public boolean hasNext() {
        return page < totalPages - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }
}
