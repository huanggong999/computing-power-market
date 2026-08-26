package com.lingyang.cloud.model.query.active;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 16:24
 */
@Data
public class SysActiveCouponQuery {

    @Schema(description = "优惠劵类型")
    private Integer couponType;

    @Schema(description = "优惠劵名称")
    private String couponName;

    /**
     * 有效开始时间
     */
    @Schema(description = "有效开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 有效结束时间
     */
    @Schema(description = "有效结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
}
