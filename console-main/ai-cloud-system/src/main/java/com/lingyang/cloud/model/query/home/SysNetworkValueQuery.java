package com.lingyang.cloud.model.query.home;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SysNetworkValueQuery {


    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "客户手机号")
    private String customerMobile;

    @Schema(description = "邮箱")
    private String email;


    /**
     * 产品名称
     */
    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "用户名称")
    private String nickname;

    @Schema(description = "用户手机号")
    private String mobile;

    @Schema(description = "支付状态")
    private Integer payStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "购买开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "购买结束时间")
    private Date endTime;

    @Schema(description = "实付价")
    private BigDecimal payPrice;

    /**
     * 表单名称
     */
    @Schema(description = "表单名称")
    private String formName;



    @Schema(description = "表单类型（1 咨询表单 2 购买表单）")
    private Integer formType;

    @Schema(description = "产品状态（1 未开通， 2 已开通， 3 已过期， 4已停线）")
    private Integer actualStatus;

}
