package com.lingyang.cloud.model.vo.applets;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/10 17:29
 */
@Data
public class AppletsIncomeListVO {

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date month;

    @Schema(description = "交易类型")
    private SysTransactionType transactionType;

}
