package com.nate.codegen.util;

import com.nate.codegen.model.Column;

public class TypeTranslateUtils {

    public static String mysqlTypeToPostgres(Column column) {
        if (column.getDbType().equals("integer") && column.isAutoIncrement()) {
            return "serial";
        }
        if (column.getDbType().equals("int") && column.isAutoIncrement()) {
            return "serial";
        }
        if (column.getDbType().equals("bigint") && column.isAutoIncrement()) {
            return "bigserial";
        }
        if (column.getDbType().equals("decimal")) {
            return "decimal(" + column.getLength() + "," + column.getNumberScale() + ")";
        }
        if (column.getDbType().equals("datetime")) {
            return "timestamp";
        }
        if (column.getDbType().contains("text")) {
            return "text";
        }
        if (column.getDbType().equals("longblob") || column.getDbType().equals("varbinary")) {
            return "bytea";
        }
        if (column.getDbType().contains("char")) {
            return column.getDbType() + "(" + column.getLength() + ")";
        }
        return column.getDbType();
    }
}
