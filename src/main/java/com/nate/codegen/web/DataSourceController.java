package com.nate.codegen.web;

import com.nate.codegen.model.DataSourceInfo;
import com.nate.codegen.service.DataSourceService;
import com.nate.codegen.util.FileExportUtils;
import com.nate.codegen.web.request.DataSourceCreateRequest;
import com.nate.codegen.web.request.DataSourceUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/ds")
public class DataSourceController {
    @Autowired
    private DataSourceService dataSourceService;

    @GetMapping
    public List<DataSourceInfo> findAll() {
        List<DataSourceInfo> ds = dataSourceService.findAll();
        return ds;
    }

    @GetMapping("/{id}")
    public DataSourceInfo findById(@PathVariable String id) {
        DataSourceInfo dataSourceInfo = dataSourceService.findById(id);

        return dataSourceInfo;
    }

    @PostMapping
    public DataSourceInfo create(@RequestBody DataSourceCreateRequest request) {
        DataSourceInfo ds = dataSourceService.create(request);
        return ds;
    }

    @PutMapping("/{id}")
    public DataSourceInfo update(@PathVariable String id, @RequestBody DataSourceUpdateRequest request) {
        DataSourceInfo ds = dataSourceService.update(id, request);
        return ds;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        dataSourceService.delete(id);
    }

    @GetMapping("/{id}:export-entities")
    public void exportEntities(@PathVariable String id, String schema, HttpServletResponse response) throws IOException {
        byte[] content = dataSourceService.exportEntities(id, schema);
        FileExportUtils.export(schema + "_models.zip", content, response);
    }

    @GetMapping("/{id}:export-crud-sql")
    public void exportCrud(@PathVariable String id, String schema, HttpServletResponse response) throws IOException {
        byte[] content = dataSourceService.exportCrud(id, schema);
        FileExportUtils.export(schema + "_crud.zip", content, response);
    }

    @GetMapping("/{id}:mysql-to-postgres-ddl")
    public void mysqlToPostgresDDL(@PathVariable String id, String schema, HttpServletResponse response) throws IOException {
        byte[] content = dataSourceService.mysqlToPostgresDDL(id, schema);
        FileExportUtils.export(schema + "_pg.sql", content, response);
    }
}
