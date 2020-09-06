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

public class PostgresTableDao implements TableDao {

    private NamedParameterJdbcTemplate jdbc;

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresTableDao.class);

    public PostgresTableDao(DataSource dataSource) {
        this.jdbc = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public List<Table> findBySchema(String schema) {
        String sql = "select table_name,obj_description(c.oid) table_comment,s.spcname \n" +
                "from information_schema.tables t \n" +
                "inner join pg_class c on t.table_name=c.relname \n" +
                "left join pg_tablespace s on c.reltablespace=s.oid\n" +
                "where table_schema=:schema";
        Map<String, Object> params = new HashMap<>();
        params.put("schema", schema);
        return jdbc.query(sql.toString(), params, (rs, i) -> {
            Table table = new Table();
            table.setName(rs.getString("table_name"));
            table.setComment(rs.getString("table_comment"));
            table.setTableSpace(rs.getString("spcname"));
            List<Column> columns = getColumns(schema, table);
            table.setColumns(columns);
            table.setIndices(getIndexs(schema, table.getName()));
            return table;
        });
    }

    private List<Index> getIndexs(String schema, String tableName) {
        String sql = "select\n" +
                "    t.relname as table_name,\n" +
                "    i.relname as index_name,\n" +
                "    a.attname as column_name,\n" +
                " ix.indisunique is_unnique,\n" +
                " ix.indisprimary is_primary\n" +
                "from\n" +
                "    pg_class t,\n" +
                "    pg_class i,\n" +
                "    pg_index ix,\n" +
                "    pg_attribute a\n" +
                "where\n" +
                "    t.oid = ix.indrelid\n" +
                "    and i.oid = ix.indexrelid\n" +
                "    and a.attrelid = t.oid\n" +
                "    and a.attnum = ANY(ix.indkey)\n" +
                "    and t.relkind = 'r'\n" +
                "    and t.relname='" + tableName + "'\n" +
                "order by\n" +
                "    t.relname,\n" +
                "    i.relname;";
        return jdbc.query(sql, Maps.newHashMap(), (rs, k) -> {
            boolean isUnique = rs.getBoolean("is_unnique");
            return Index.builder().name(rs.getString("index_name")).columnNames(rs.getString("column_name")).isUnique(isUnique).build();
        });
    }

    private List<Column> getColumns(String schema, Table table) {
        Map<String, Object> columnParams = new HashMap<>();
        columnParams.put("schema", schema);
        columnParams.put("tableName", table.getName());
        String sql = "select column_name,data_type,\n" +
                "coalesce(character_maximum_length,numeric_precision,-1) as Length,numeric_scale as NUMERIC_SCALE,\n" +
                "is_nullable,column_default,\n" +
                "case  when position('nextval' in column_default)>0 then 1 else 0 end as IsIdentity, \n" +
                "b.pk_name PK_NAME,c.DeText as column_comment \n" +
                "from information_schema.columns \n" +
                "left join (\n" +
                "    select pg_attr.attname as colname,pg_constraint.conname as pk_name from pg_constraint  \n" +
                "    inner join pg_class on pg_constraint.conrelid = pg_class.oid \n" +
                "    inner join pg_attribute pg_attr on pg_attr.attrelid = pg_class.oid and  pg_attr.attnum = pg_constraint.conkey[1] \n" +
                "    inner join pg_type on pg_type.oid = pg_attr.atttypid\n" +
                "    where pg_class.relname = :tableName and pg_constraint.contype='p' \n" +
                ") b on b.colname = information_schema.columns.column_name\n" +
                "left join (\n" +
                "    select attname,description as DeText from pg_class\n" +
                "    left join pg_attribute pg_attr on pg_attr.attrelid= pg_class.oid\n" +
                "    left join pg_description pg_desc on pg_desc.objoid = pg_attr.attrelid and pg_desc.objsubid=pg_attr.attnum\n" +
                "    where pg_attr.attnum>0 and pg_attr.attrelid=pg_class.oid and pg_class.relname=:tableName \n" +
                ")c on c.attname = information_schema.columns.column_name\n" +
                "where table_schema='public' and table_name=:tableName order by ordinal_position asc";
        return jdbc.query(sql, columnParams, (crs, k) -> {
            String dataType = crs.getString("data_type");
            String name = crs.getString("column_name");
            String comment = crs.getString("column_comment");
            String length = crs.getString("Length");
            boolean autoIncrement = crs.getString("column_default") == null ? false : crs.getString("column_default").contains("nextval");
            boolean isPk = crs.getString("PK_NAME") != null;
            String numScale = crs.getString("NUMERIC_SCALE");
            boolean isNullable = crs.getString("is_nullable").equals("YES");
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
        if (dbType.equalsIgnoreCase("integer")) {
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
        if (dbType.equalsIgnoreCase("character varying")) {
            return "String";
        }
        if (dbType.contains("text")) {
            return "String";
        }
        if (dbType.equalsIgnoreCase("character")) {
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
        if (dbType.equalsIgnoreCase("timestamp without time zone")) {
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
        if (dbType.equals("numeric")) {
            return "BigDecimal";
        }
        LOGGER.warn(" unknow data type:" + dbType);
        return "unknow";
    }
}
