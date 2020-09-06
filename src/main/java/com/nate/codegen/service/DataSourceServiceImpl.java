package com.nate.codegen.service;

import com.google.common.base.Charsets;
import com.nate.codegen.dao.DataSourceInfoDao;
import com.nate.codegen.dao.TableDao;
import com.nate.codegen.dao.TableDaoFactory;
import com.nate.codegen.gen.*;
import com.nate.codegen.model.DataSourceInfo;
import com.nate.codegen.model.Table;
import com.nate.codegen.util.Names;
import com.nate.codegen.web.request.DataSourceCreateRequest;
import com.nate.codegen.web.request.DataSourceUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@Slf4j
public class DataSourceServiceImpl implements DataSourceService {
    @Autowired
    private DataSourceInfoDao dao;
    @Autowired
    private EntityGenerator entityGenerator;

    @Override
    public List<DataSourceInfo> findAll() {
        return dao.findAll();
    }

    @Override
    public DataSourceInfo findById(String id) {
        return dao.findById(id);
    }

    @Override
    public DataSourceInfo create(DataSourceCreateRequest request) {
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setDriverClassName(request.getDriverClassName());
        dataSourceInfo.setName(request.getName());
        dataSourceInfo.setPassword(request.getPassword());
        dataSourceInfo.setUsername(request.getUsername());
        dataSourceInfo.setUrl(request.getUrl());
        dataSourceInfo.setId(UUID.randomUUID().toString());
        return dao.create(dataSourceInfo);
    }

    @Override
    public DataSourceInfo update(String id, DataSourceUpdateRequest request) {
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setId(id);
        dataSourceInfo.setDriverClassName(request.getDriverClassName());
        dataSourceInfo.setName(request.getName());
        dataSourceInfo.setPassword(request.getPassword());
        dataSourceInfo.setUsername(request.getUsername());
        dataSourceInfo.setUrl(request.getUrl());
        return dao.update(dataSourceInfo);
    }

    @Override
    public void delete(String id) {
        dao.delete(id);
    }

    @Override
    public byte[] exportEntities(String id, String schema) {
        DataSourceInfo dataSourceInfo = dao.findById(id);
        TableDao tableDao = TableDaoFactory.create(dataSourceInfo);
        List<Table> tables = tableDao.findBySchema(schema);
        log.info("tables:" + tables.size());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Table table : tables) {
                String entityText = entityGenerator.genEntity(table);
                ZipEntry entry = new ZipEntry(Names.toClassName(table.getName()) + ".java");
                zos.putNextEntry(entry);
                zos.write(entityText.getBytes(Charsets.UTF_8));
                zos.closeEntry();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return baos.toByteArray();
    }


    @Override
    public byte[] mysqlToPostgresDDL(String id, String schema) {
        DataSourceInfo dataSourceInfo = dao.findById(id);
        TableDao tableDao = TableDaoFactory.create(dataSourceInfo);
        List<Table> tables = tableDao.findBySchema(schema);
        StringBuilder sb = new StringBuilder();
        tables.forEach(e -> {
            String tableDDL =new DefaultMysqlPostgresGenerator().gen(e);
            sb.append(tableDDL);
        });
        return sb.toString().getBytes(Charsets.UTF_8);
    }

    @Override
    public byte[] exportCrud(String id, String schema) {
        SqlGenerator sqlGenerator = new CrudSqlGenerator();
        DataSourceInfo dataSourceInfo = dao.findById(id);
        TableDao tableDao = TableDaoFactory.create(dataSourceInfo);
        List<Table> tables = tableDao.findBySchema(schema);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (Table table : tables) {
                String entityText = sqlGenerator.gen(table);
                ZipEntry entry = new ZipEntry(table.getName() + ".sql");
                zos.putNextEntry(entry);
                zos.write(entityText.getBytes(Charsets.UTF_8));
                zos.closeEntry();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return baos.toByteArray();
    }

}
