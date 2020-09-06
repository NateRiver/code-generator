package com.nate.codegen.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@Builder
@Slf4j
public class Column {
    private String name;
    private String comment;
    private String javaType;
    private String dbType;
    private String length;
    private boolean isPK;//
    private boolean autoIncrement;//
    private String numberScale;
    private boolean isNullable;
}
