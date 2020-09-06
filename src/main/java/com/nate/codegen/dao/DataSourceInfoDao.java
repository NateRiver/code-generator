package com.nate.codegen.dao;

import com.nate.codegen.model.DataSourceInfo;

import java.util.List;

/**
 * DataSource Dao
 *
 * @author Nate
 */
public interface DataSourceInfoDao {

    List<DataSourceInfo> findAll();

    DataSourceInfo findById(String id);

    DataSourceInfo create(DataSourceInfo dataSourceInfo);

    DataSourceInfo update(DataSourceInfo dataSourceInfo);

    void delete(String id);
}
