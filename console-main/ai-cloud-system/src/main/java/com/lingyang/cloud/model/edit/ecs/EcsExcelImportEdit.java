package com.lingyang.cloud.model.edit.ecs;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 16:58
 */
@Data
public class EcsExcelImportEdit implements Serializable {

    @Serial
    private static final long serialVersionUID = -3564783227627181622L;
    /**
     * 服务器规格
     */
    @Schema(description = "服务器规格")
    @ExcelProperty("实例规格")
    private String ecsScale;

    /**
     * cpu数量
     */
    @Schema(description = "cpu数量")
    @ExcelProperty("vCPU")
    private Integer cpuNumber;

    /**
     * 内存大小
     */
    @Schema(description = "内存大小")
    @ExcelProperty("内存 (GiB)")
    private Integer memorySize;

    /**
     * cpu型号
     */
    @Schema(description = "cpu型号")
    @ExcelProperty("处理器型号")
    private String cpuModel;

    /**
     * gpu型号
     */
    @Schema(description = "gpu型号")
    @ExcelProperty("GPU型号")
    private String gpuModel;

    /**
     * gpu内存
     */
    @Schema(description = "gpu内存")
    @ExcelProperty("GPU显存(GB)")
    private String gpuMemory;


    /**
     * 可用区
     */
    @ExcelProperty("地域")
    private String regions;

    /**
     * 按量计费
     */
    @Schema(description = "按量计费价格")
    @TableField("hours_price")
    @ExcelProperty("按量计费 (小时)")
    private BigDecimal hoursPrice;

    @ExcelProperty("公网费用（元/时/M）")
    @TableField("ip_price")
    private BigDecimal ipPrice;

    /**
     * 包年包月
     */
    @Schema(description = "包年包月价格")
    @TableField("month_price")
    @ExcelProperty("包月")
    private BigDecimal monthPrice;

    /**
     * 1年
     */
    @Schema(description = "1年价格")
    @TableField("one_year_price")
    @ExcelProperty("包1年")
    private BigDecimal oneYearPrice;

    /**
     * 2年
     */
    @Schema(description = "2年价格")
    @TableField("two_year_price")
    @ExcelProperty("包2年")
    private BigDecimal twoYearPrice;

    /**
     * 3年
     */
    @Schema(description = "3年价格")
    @TableField("three_year_price")
    @ExcelProperty("包3年")
    private BigDecimal threeYearPrice;
}
