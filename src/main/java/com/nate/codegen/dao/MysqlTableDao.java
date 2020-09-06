package com.nate.codegen.dao;

import com.google.common.collect.Maps;
import com.nate.codegen.model.Column;
import com.nate.codegen.model.Index;
import com.nate.codegen.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlTableDao implements TableDao {

    private NamedParameterJdbcTemplate jdbc;

    public MysqlTableDao(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlTableDao.class);

    @Override
    public List<Table> findBySchema(String schema) {
        StringBuilder sql = new StringBuilder("select table_name,table_comment from information_schema.tables where table_schema=:schema ");
        Map<String, Object> params = new HashMap<>();
        params.put("schema", schema);
        return jdbc.query(sql.toString(), params, (rs, i) -> {
            Table table = new Table();
            table.setName(rs.getString("table_name"));
            table.setComment(rs.getString("table_comment"));
            List<Column> columns = getColumns(schema, table);
            table.setColumns(columns);
            table.setIndices(getIndexs(schema, table.getName()));
            return table;
        });
    }

    private List<Index> getIndexs(String schema, String tableName) {
        return jdbc.query("show index from " + tableName, Maps.newHashMap(), (rs, k) -> {
            boolean isUnique = rs.getString("Non_unique").equals("0");
            return Index.builder().name(rs.getString("key_name")).columnNames(rs.getString("column_name")).isUnique(isUnique).build();
        });
    }

    private List<Column> getColumns(String schema, Table table) {
        Map<String, Object> columnParams = new HashMap<>();
        columnParams.put("schema", schema);
        columnParams.put("tableName", table.getName());
        return jdbc.query("SELECT column_name,column_comment,column_key,data_type,character_maximum_length" +
                ",EXTRA,COLUMN_KEY,NUMERIC_PRECISION,NUMERIC_SCALE,IS_NULLABLE FROM " +
                " information_schema.columns WHERE table_schema=:schema AND table_name=:tableName order by ordinal_position", columnParams, (crs, k) -> {
            String dataType = crs.getString("data_type");
            String name = crs.getString("column_name");
            String comment = crs.getString("column_comment");
            String charLength = crs.getString("character_maximum_length");
            boolean autoIncrement = crs.getString("EXTRA").equals("auto_increment");
            boolean isPk = crs.getString("COLUMN_KEY").equals("PRI");
            String numLen = crs.getString("NUMERIC_PRECISION");
            String numScale = crs.getString("NUMERIC_SCALE");
            boolean isNullable = crs.getString("IS_NULLABLE").equals("YES");
            String length = charLength == null ? numLen : charLength;
            return Column.builder()
                    .comment(comment)
                    .name(name)
                    .dbType(dataType)
                    .javaType(getJavaType(dataType))
                    .length(length).numberScale(numScale)
                    .isNullable(isNullable).isPK(isPk).autoIncrement(autoIncrement).build();

        });
    }

    public String getJavaType(String dbType) {
        if (dbType.equalsIgnoreCase("bigint")) {
            return "Long";
        }
        if (dbType.equalsIgnoreCase("int")) {
            return "Integer";
        }
        if (dbType.equalsIgnoreCase("smallint")) {
            return "Integer";
        }
        if (dbType.equalsIgnoreCase("mediumint")) {
            return "Integer";
        }
        if (dbType.equalsIgnoreCase("varchar")) {
            return "String";
        }
        if (dbType.contains("text")) {
            return "String";
        }
        if (dbType.equalsIgnoreCase("char")) {
            return "String";
        }
        if (dbType.equalsIgnoreCase("timestamp")) {
            return "Date";
        }
        if (dbType.equalsIgnoreCase("datetime")) {
            return "Date";
        }
        if (dbType.equalsIgnoreCase("date")) {
            return "Date";
        }
        if (dbType.equalsIgnoreCase("decimal")) {
            return "BigDecimal";
        }
        if (dbType.equalsIgnoreCase("tinyint")) {
            return "Integer";
        }
        if (dbType.contains("blob")) {
            return "byte[]";
        }
        if (dbType.contains("varbinary")) {
            return "byte[]";
        }
        LOGGER.warn(" unknow data type:" + dbType);
        return "unknow";
    }
}
