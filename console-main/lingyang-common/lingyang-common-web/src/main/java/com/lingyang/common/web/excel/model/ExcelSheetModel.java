package com.lingyang.common.web.excel.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/11/3 14:29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSheetModel<T> {

    /**
     * sheet名称
     */
    @Schema(description = "sheet名称")
    private String sheetName;

    /**
     * sheet数据列表
     */
    @Schema(description = "sheet数据列表")
    private List<T> data;

}