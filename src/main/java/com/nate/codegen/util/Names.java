package com.nate.codegen.util;

import com.google.common.base.CaseFormat;

/**
 * Names util
 *
 * @author Nate
 */
public class Names {

    private Names() {
    }

    public static String toClassName(String input) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, input);
    }

    public static String toFieldName(String input) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input);
    }
}
