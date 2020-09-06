package com.nate.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Index {
    private String name;
    private String columnNames;
    private boolean isUnique;
}
