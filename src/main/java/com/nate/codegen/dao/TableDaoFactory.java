package com.nate.codegen.dao;

import com.google.common.collect.Maps;
import com.nate.codegen.model.DataSourceInfo;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Map;

public class TableDaoFactory {
    public static final Map<String,HikariDataSource> DS= Maps.newConcurrentMap();

    public static TableDao create(DataSourceInfo dataSourceInfo){

        HikariDataSource dataSource= DS.get(dataSourceInfo.getId());
        if(dataSource==null){
            dataSource=new HikariDataSource();
            dataSource.setJdbcUrl(dataSourceInfo.getUrl());
            dataSource.setUsername(dataSourceInfo.getUsername());
            dataSource.setPassword(dataSourceInfo.getPassword());
            dataSource.setDriverClassName(dataSourceInfo.getDriverClassName());
            DS.put(dataSourceInfo.getId(),dataSource);
        }

        if(dataSourceInfo.getUrl().contains("mysql")){
            return new MysqlTableDao(dataSource);
        }
        if(dataSourceInfo.getUrl().contains("postgres")){
            return new PostgresTableDao(dataSource);
        }
        throw new IllegalArgumentException("not support");
    }
}
