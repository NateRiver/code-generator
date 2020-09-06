package com.nate.codegen.gen;

import com.nate.codegen.model.Table;

public interface SqlGenerator {

    String gen(Table table);
}
