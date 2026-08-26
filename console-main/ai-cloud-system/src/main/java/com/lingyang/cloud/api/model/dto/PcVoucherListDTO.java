package com.lingyang.cloud.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/26 17:47
 */
@Data
public class PcVoucherListDTO {

    @Schema(description = "充值金额")
    private BigDecimal rechargeAmount;

    @Schema(description = "代金券金额")
    private BigDecimal couponAmount;



    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "奖励代金券开始时间")
    private Date couponStartTime;


    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "奖励代金券结束时间")
    private Date couponEndTime;
}
