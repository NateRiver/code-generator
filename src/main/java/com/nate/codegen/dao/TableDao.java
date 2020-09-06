package com.nate.codegen.dao;

import com.nate.codegen.model.Table;

import java.util.List;

public interface TableDao {

    List<Table> findBySchema(String schema);
}
