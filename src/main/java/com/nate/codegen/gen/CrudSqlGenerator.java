package com.nate.codegen.gen;

import com.nate.codegen.model.Table;
import com.nate.codegen.util.Names;

import java.util.StringJoiner;

public class CrudSqlGenerator implements SqlGenerator {
    @Override
    public String gen(Table table) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("\n");
        StringJoiner fields = new StringJoiner(",");
        StringJoiner cols = new StringJoiner(",");
        table.getColumns().forEach(e -> {
            fields.add(":" + Names.toFieldName(e.getName()));
            cols.add(e.getName());
        });
        sqlBuilder.append("======query sql==============");
        sqlBuilder.append("select " + cols.toString() + " from " + table.getName());
        sqlBuilder.append("======insert sql===========\n");
        sqlBuilder.append("insert into " + table.getName() + " values(" + fields.toString() + ")\n");
        sqlBuilder.append("insert into " + table.getName() + "(" + cols.toString() + ")values(" + fields.toString() + ")\n");
        sqlBuilder.append("======java fields\n");
        StringJoiner javaFields = new StringJoiner(",");
        table.getColumns().forEach(e -> {
            javaFields.add(Names.toFieldName(e.getName()));
        });
        sqlBuilder.append(javaFields.toString() + "\n");
        return sqlBuilder.toString();

    }
}
