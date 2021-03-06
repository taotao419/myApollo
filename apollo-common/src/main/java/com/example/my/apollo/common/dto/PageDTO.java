package com.example.my.apollo.common.dto;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Pageable;

/**
 * PageDTO
 */
public class PageDTO<T> {

    private final long total;
    private final List<T> content;
    private final int page;
    private final int size;

    public PageDTO(List<T> content, Pageable pageable, long total) {
        this.total = total;
        this.content = content;
        this.page = pageable.getPageNumber();
        this.size = pageable.getPageSize();
    }

    public long getTotal() {
        return total;
    }

    public List<T> getContent() {
        //TODO : unmodifiableList 什么意思?
        return Collections.unmodifiableList(content);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean hasContent() {
        return content != null && content.size() > 0;
    }
}