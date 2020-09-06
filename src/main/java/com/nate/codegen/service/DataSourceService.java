package com.nate.codegen.service;

import com.nate.codegen.model.DataSourceInfo;
import com.nate.codegen.web.request.DataSourceCreateRequest;
import com.nate.codegen.web.request.DataSourceUpdateRequest;

import java.util.List;

/**
 * DataSource service
 *
 * @author Nate
 */
public interface DataSourceService {
    /**
     * return all DataSource
     *
     * @return  all DataSource
     */
    List<DataSourceInfo> findAll();

    /**
     * return DataSource by its id
     *
     * @param id not null
     * @return return DataSource of id
     */
    DataSourceInfo findById(String id);

    /**
     * create a DataSource
     * @param request the DataSource info
     * @return the data source created
     */
    DataSourceInfo create(DataSourceCreateRequest request);

    DataSourceInfo update(String id, DataSourceUpdateRequest request);

    /**
     * delete a DataSource by id
     * @param id
     */
    void delete(String id);

    byte[] exportEntities(String id, String schema);

    byte[] mysqlToPostgresDDL(String id, String schema);

    byte[] exportCrud(String id, String schema);

}
