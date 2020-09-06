package com.nate.codegen.gen;

import com.google.common.base.Strings;
import com.nate.codegen.model.Table;
import com.nate.codegen.util.Names;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author
 */
@Component
public class EntityGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityGenerator.class);


    public String genEntity(Table table) {
        LOGGER.info("create entity of table:" + table.getName());
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            pw.println();
            pw.println("import org.springframework.data.relational.core.mapping.Table;");
            pw.println("/**");
            pw.println("* table name：" + table.getName());
            pw.println("* table comment：" + table.getComment());
            pw.println("* @author //TODO");
            pw.println("*");
            pw.println("*/");
            pw.println("@Table(\"" + table.getName() + "\")");
            pw.println("public class " + Names.toClassName(table.getName()) + "{");
            table.getColumns().forEach(e -> {
                pw.println("//" + Strings.nullToEmpty(e.getComment()));
                pw.println("private " + e.getJavaType() + " " + Names.toFieldName(e.getName()) + ";");
                pw.println();
            });
            pw.println("}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }
}
