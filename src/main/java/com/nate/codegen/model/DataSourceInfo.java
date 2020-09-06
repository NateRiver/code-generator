package com.nate.codegen.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class DataSourceInfo {
    private String id;
    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;

}
