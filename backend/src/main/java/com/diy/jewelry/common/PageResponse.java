package com.diy.jewelry.common;

import java.util.Collections;
import java.util.List;
import lombok.Data;

@Data
public class PageResponse<T> {

    private long current;
    private long size;
    private long total;
    private List<T> records = Collections.emptyList();

    public static <T> PageResponse<T> of(long current, long size, long total, List<T> records) {
        PageResponse<T> response = new PageResponse<>();
        response.setCurrent(current);
        response.setSize(size);
        response.setTotal(total);
        response.setRecords(records);
        return response;
    }
}
