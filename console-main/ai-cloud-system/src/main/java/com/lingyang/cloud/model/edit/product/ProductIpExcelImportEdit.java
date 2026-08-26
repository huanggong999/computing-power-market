package com.lingyang.cloud.model.edit.product;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/11 14:23
 */
@Data
public class ProductIpExcelImportEdit implements Serializable {

    @Serial
    private static final long serialVersionUID = -3564783227627181622L;

    /**
     * 内网IP
     */
    @Schema(description = "内网IP")
    @ExcelProperty("内网IP")
    private String ip;

    /**
     * 公网IP
     */
    @Schema(description = "公网IP")
    @ExcelProperty("公网IP")
    private String publicIp;


}
