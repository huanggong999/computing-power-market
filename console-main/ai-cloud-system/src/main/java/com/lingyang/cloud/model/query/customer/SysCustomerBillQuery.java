package com.lingyang.cloud.model.query.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 14:51
 */
@Data
public class SysCustomerBillQuery {

    @Schema(description = "订单号")
    private String billNo;

    @Schema(description = "开始查询时间： 格式yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束查询时间： 格式yyyy-MM-dd")
    private Date endDate;

    @Schema(description = "月份")
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM")
    private Date month;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "客户名称/手机号模糊查询")
    private String customerInfoQuery;
}
