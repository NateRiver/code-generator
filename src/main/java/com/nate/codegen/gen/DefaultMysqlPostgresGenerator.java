package com.nate.codegen.gen;

import com.nate.codegen.model.Table;
import com.nate.codegen.util.TypeTranslateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

public class DefaultMysqlPostgresGenerator implements MysqlToPostgresGenerator {

    public String gen(Table table) {
        String tableName = table.getName().toLowerCase();
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS " + tableName + ";\n");
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " ( \n");
        table.getColumns().forEach(e -> {
            if (e.isNullable()) {
                sql.append("     " + e.getName().toLowerCase() + " " + TypeTranslateUtils.mysqlTypeToPostgres(e) + ",\n");
            } else {
                sql.append("    " + e.getName().toLowerCase() + " " + TypeTranslateUtils.mysqlTypeToPostgres(e) + " NOT NULL,\n");
            }

        });
        String pri = table.getColumns().stream().filter(e -> e.isPK()).map(e -> e.getName().toLowerCase()).collect(Collectors.joining(","));
        if (StringUtils.isNotBlank(pri)) {
            sql.append(" CONSTRAINT pk_" + tableName + "  PRIMARY KEY" + "(" + pri + ")\n");
        } else {
            sql.deleteCharAt(sql.length() - 2);
        }
        sql.append("); \n");
        sql.append("COMMENT ON TABLE " + tableName + " IS '" + table.getComment() + "';\n");
        sb.append(sql);
        table.getColumns().forEach(e -> {
            if (StringUtils.isBlank(e.getComment())) {
                return;
            }
            String columnCommentSql = "COMMENT ON COLUMN " + tableName + "." + e.getName().toLowerCase() + " IS '" + e.getComment() + "';\n";
            sb.append(columnCommentSql);
        });
        return sb.toString();

    }


}
