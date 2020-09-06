package com.nate.codegen.model;

import lombok.Data;

import java.util.List;

@Data
public class Table {

    private String name;

    private String comment;

    private List<Column> columns;

    private List<Index> indices;

    private String tableSpace;

    private String tableSchema;
}
