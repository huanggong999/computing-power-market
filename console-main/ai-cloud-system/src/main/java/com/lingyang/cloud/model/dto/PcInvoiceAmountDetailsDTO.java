package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/11 16:44
 */
@Data
public class PcInvoiceAmountDetailsDTO {

    /**
     * 可开发票金额
     */
    @Schema(description = "可开发票金额")
    private BigDecimal amount;

    /**
     * 可开发票总额
     */
    @Schema(description = "可开发票总额")
    private BigDecimal totalAmount;

    /**
     * 已开发票金额
     */
    @Schema(description = "已开发票金额")
    private BigDecimal alreadyAmount;

    /**
     * 不可开发票金额
     */
    @Schema(description = "不可开发票金额")
    private BigDecimal cannotAmount;
}
