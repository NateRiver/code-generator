package com.nate.codegen.dao;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nate.codegen.model.DataSourceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("file")
@Slf4j
public class JsonFileDataSourceInfoDao implements DataSourceInfoDao {
    @Value("${ds.store.path}")
    private String path;

    @Override
    public List<DataSourceInfo> findAll() {
        File store = new File(path);
        if (!store.exists()) {
            return Collections.emptyList();
        }
        try {
            byte[] bytes = Files.toByteArray(new File(path));
            Gson gson = new Gson();
            return gson.fromJson(new String(bytes, Charsets.UTF_8), new TypeToken<ArrayList<DataSourceInfo>>() {
            }.getType());
        } catch (IOException e) {
            log.error("", e);
            return Collections.emptyList();
        }
    }

    @Override
    public DataSourceInfo findById(String id) {
        return findAll().stream().filter(e -> e.getId().equals(id)).findFirst().orElseThrow(RuntimeException::new);
    }

    @Override
    public DataSourceInfo create(DataSourceInfo dataSourceInfo) {
        List<DataSourceInfo> ds = Lists.newArrayList(findAll());
        ds.add(dataSourceInfo);
        Gson gson = new Gson();
        String content = gson.toJson(ds);
        try {
            Files.write(content.getBytes(Charsets.UTF_8), new File(path));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return dataSourceInfo;
    }

    @Override
    public DataSourceInfo update(DataSourceInfo dataSourceInfo) {
        List<DataSourceInfo> ds = Lists.newArrayList();
        findAll().forEach(e -> {
            if (e.getId().equals(dataSourceInfo.getId())) {
                ds.add(dataSourceInfo);
            } else {
                ds.add(e);
            }

        });
        Gson gson = new Gson();
        String content = gson.toJson(ds);
        try {
            Files.write(content.getBytes(Charsets.UTF_8), new File(path));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return dataSourceInfo;
    }

    @Override
    public void delete(String id) {

        List<DataSourceInfo> ds = findAll().stream()
                .filter(e -> !e.getId().equals(id))
                .collect(Collectors.toList());
        Gson gson = new Gson();
        String content = gson.toJson(ds);
        try {
            Files.write(content.getBytes(Charsets.UTF_8), new File(path));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
