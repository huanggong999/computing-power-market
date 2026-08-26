package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/14 10:50
 */
@Data
public class PcInvoiceManageVO {

    @Schema(description = "账单Id")
    private List<Long> billIds;

    /**
     * 发票抬头ID
     */
    @Schema(description = "发票抬头ID")
    @NotNull(message = "发票抬头ID不能为空")
    private Integer invoiceTitleId;

    /**
     * 电子邮箱ID
     */
    @Schema(description = "电子邮箱ID")
    @NotNull(message = "电子邮箱ID不能为空")
    private Integer invoiceEmailId;


    /**
     * 发票金额
     */
    @Schema(description = "发票金额")
    @NotNull(message = "发票金额不能为空")
    private BigDecimal invoicePrice;


    /**
     * 开票类型
     */
    @Schema(description = "开票类型（1按账期开票 2按消费时间开票）")
    @NotNull(message = "开票类型不能为空")
    private Integer type;


    /**
     * 发票备注
     */
    @Schema(description = "发票备注")
    private String remark;
}
