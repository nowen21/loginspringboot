package com.nowensoft.softcontab.datatable.dto;

import lombok.Data;

@Data
public class DataTableRequestDTO {
    private int draw;
    private int start;
    private int length;
    private String searchValue;
    private String sortColumn;
    private String sortDirection;
}
