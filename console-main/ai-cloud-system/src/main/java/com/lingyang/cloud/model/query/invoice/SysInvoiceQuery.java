package com.lingyang.cloud.model.query.invoice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/13 11:12
 */
@Data
public class SysInvoiceQuery {

    @Schema(description = "发票类型（1按账期开票 2按消费时间开票 3提前开发票）")
    private Integer type;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "开始月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date startMonth;

    @Schema(description = "结束月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date endMonth;

    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDay;

    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDay;
}
