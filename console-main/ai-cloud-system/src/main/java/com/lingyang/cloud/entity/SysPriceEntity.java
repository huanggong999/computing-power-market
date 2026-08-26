package com.lingyang.cloud.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/7 16:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class SysPriceEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6847078756712959575L;
    /**
     * 按量计费
     */
    @Schema(description = "按量计费价格")
    @TableField("hours_price")
    @ExcelProperty("按量计费 (小时)")
    private BigDecimal hoursPrice;

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
