package com.lingyang.cloud.model.query.customer;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:14
 */
@Data
public class SysCustomerEcsWorkQuery {

    @Schema(description = "用户名手机号搜索key")
    private String customerInfoQueryKey;

    @Schema(description = "开始查询时间： 格式yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "结束查询时间： 格式yyyy-MM-dd")
    private Date endDate;
}
