package com.nate.codegen.web.request;

import lombok.Data;

@Data
public class DataSourceCreateRequest {

    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
