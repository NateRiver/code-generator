package com.nate.codegen.web.request;

import lombok.Data;

@Data
public class DataSourceUpdateRequest {

    private String name;
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
