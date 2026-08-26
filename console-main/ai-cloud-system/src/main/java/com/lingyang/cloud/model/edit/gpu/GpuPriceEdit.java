package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * GPU价格编辑参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU价格编辑参数")
public class GpuPriceEdit {

    @Schema(description = "计费方式 on_demand/hourly/daily/weekly/monthly")
    private String billingType;

    @Schema(description = "单价")
    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    @Schema(description = "折扣价")
    private BigDecimal discountPrice;

    @Schema(description = "折扣率")
    private String discountRate;
}
