package com.nowensoft.softcontab.datatable.dto;

import java.util.List;

import lombok.Data;

@Data
public class DataTableResponseDTO<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private List<T> data;
}
