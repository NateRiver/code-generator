package com.nate.codegen.gen;

import com.nate.codegen.model.Table;

public interface MysqlToPostgresGenerator {

     String gen(Table table);
}
