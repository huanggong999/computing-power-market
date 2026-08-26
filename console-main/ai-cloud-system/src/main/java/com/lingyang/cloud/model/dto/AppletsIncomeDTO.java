package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/10 10:56
 */
@Data
public class AppletsIncomeDTO {
    @Schema(description = "入账合计")
    private BigDecimal incomeTotal;

    @Schema(description = "出账合计")
    private BigDecimal outTotal;

    @Schema(description = "账单集合")
    private List<AppletsIncomeListDTO> billList;
}
