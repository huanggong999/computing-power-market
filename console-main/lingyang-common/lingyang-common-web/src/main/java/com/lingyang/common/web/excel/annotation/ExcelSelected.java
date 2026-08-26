package com.lingyang.common.web.excel.annotation;

import com.lingyang.common.web.excel.handler.ExcelDynamicSelect;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/9/13 14:03
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSelected {

    /**
     * 固定下拉内容
     */
    String[] source() default {};

    /**
     * 动态下拉内容
     */
    Class<? extends ExcelDynamicSelect>[] sourceClass() default {};

    /**
     * 设置下拉框的起始行，默认为第二行
     */
    int fistRow() default 1;

    /**
     * 设置下拉框的结束行，默认为最后一行
     */
    int lasWor() default 0x10000;
}