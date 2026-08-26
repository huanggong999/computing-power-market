package com.lingyang.common.web.excel.model;

import com.lingyang.common.core.utils.ClassUtils;
import com.lingyang.common.web.excel.annotation.ExcelSelected;
import com.lingyang.common.web.excel.handler.ExcelDynamicSelect;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/9/13 14:00
 */
@Data
public class ExcelSelectedResolve {
    /**
     * 下拉内容
     */
    private String[] source;
    /**
     * 设置下拉框的起始行， 默认为第二行
     */
    private int firstRow;
    /**
     * 设置下拉框的结束行， 默认为第一行
     */
    private  int  lastRow;

    /**
     * 下拉列名
     */
    private String columnName;

    public String[]  resolveSelectedSource(ExcelSelected excelselected) {
        if (excelselected == null ) {
            return null;
        }
        String[] source = excelselected.source();
        if (source.length > 0) {
            return source;
        }
        Class<? extends ExcelDynamicSelect>[] sourceClass = excelselected.sourceClass();
        return sourceClass == null ? null :ClassUtils.newInstance(sourceClass[0]).getSource();
    }
}
