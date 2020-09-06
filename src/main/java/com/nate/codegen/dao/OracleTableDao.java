package com.nate.codegen.dao;

import com.nate.codegen.model.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

public class OracleTableDao implements TableDao {
    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    private static final Logger LOGGER = LoggerFactory.getLogger(OracleTableDao.class);
    @Override
    public List<Table> findBySchema(String schema) {
        throw new UnsupportedOperationException();
    }

}
