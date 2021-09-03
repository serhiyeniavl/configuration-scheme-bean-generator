package com.sijodji.context.util;

import com.google.common.base.CaseFormat;

public class BeanNameUtils {

    public static String getBeanName(String eventName, Class<?> aClass) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, eventName).concat(aClass.getSimpleName());
    }

    public static String toUpperUnderscoreFormat(String eventName) {
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_UNDERSCORE, eventName);
    }
}
