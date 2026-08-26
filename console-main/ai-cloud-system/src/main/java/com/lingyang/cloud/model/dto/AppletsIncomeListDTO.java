package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/10 10:56
 */
@Data
public class AppletsIncomeListDTO {
    @Schema(description = "账单名称")
    private String billName;

    @Schema(description = "交易类型")
    private SysTransactionType transactionType;

    @Schema(description = "变更金额")
    private BigDecimal changeAmount;

    @Schema(description = "可用余额")
    private BigDecimal availableBalance;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
