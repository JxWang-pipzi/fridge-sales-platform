package com.fridge.sales.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String title() default "";

    BusinessType businessType() default BusinessType.OTHER;

    int operatorType() default 0;

    boolean isSaveRequestData() default true;

    boolean isSaveResponseData() default true;

    enum BusinessType {
        OTHER(0, "其他"),
        INSERT(1, "新增"),
        UPDATE(2, "修改"),
        DELETE(3, "删除"),
        GRANT(4, "授权"),
        EXPORT(5, "导出"),
        IMPORT(6, "导入"),
        FORCE(7, "强退"),
        CLEAN(8, "清空");

        private final int code;
        private final String desc;

        BusinessType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
}
