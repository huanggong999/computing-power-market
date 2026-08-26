package com.lingyang.cloud.model.vo.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.common.web.serializer.CustomerBigDecimalSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 15:38
 */
@Data
public class SysCustomerBillOverviewVO {

    @Schema(description = "账单Id")
    private Long billId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户手机号")
    private String phone;

    @Schema(description = "账期")
    private String bill;

    @Schema(description = "账期最后一天")
    private String billDate;
    /**
     * 原价
     */
    @Schema(description = "原价")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal originalPrice;

    /**
     * 溢价价格
     */
    @Schema(description = "溢价价格")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal premiumPrice;

    /**
     * 用户折扣金额
     */
    @Schema(description = "用户折扣金额")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal userDiscountAmount;

    /**
     * 应付价格
     */
    @Schema(description = "应付价格")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal payPrice;

    /**
     * 代金券金额
     */
    @Schema(description = "代金券金额")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal voucherAmount;

    @Schema(description = "授信额金额")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal creditLineAmount;


    /**
     * 余额支付金额
     */
    @Schema(description = "余额支付金额")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal balancePayAmount;

    /**
     * 欠费金额
     */
    @Schema(description = "欠费金额")
    @JsonSerialize(using = CustomerBigDecimalSerialize.class)
    private BigDecimal arrearsAmount;

    /**
     * 资源类型
     */
    @Schema(description = "资源类型")
    @TableField("source_type")
    private SourceTypeEnum sourceType;

    @Schema(description = "账单数量")
    private Long billNumber;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "账单id集合")
    private String billIds;

    @Schema(description = "标签(0未开票 1已开票)")
    private Integer tag;
}
