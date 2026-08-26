package com.lingyang.cloud.model.query.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 16:16
 */
@Data
public class SysOrderQuery {

    @Schema(description = "订单类型")
    private OrderTypeEnum orderType;

    @Schema(description = "订单状态")
    private OrderStatusEnum orderStatus;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "客户信息模糊查询key，手机号和名称")
    private String customerInfoQueryKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private Date endTime;

    @Schema(description = "充值id")
    private Long rechargeId;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;

    @Schema(description = "客户名称（购买产品时补充字段）")
    private String agiCustomerName;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "带宽")
    private Integer bandwidth;

    @Schema(description = "产品状态（1 未开通， 2 已开通， 3 已过期， 4已停线）")
    private Integer actualStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "产品到期查询开始时间")
    private Date actualAgiExpireStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "产品到期查询结束时间")
    private Date actualAgiExpireEndTime;

    @Schema(description = "产品开通查询开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actualAgiOpenStartTime;

    @Schema(description = "产品开通查询结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date actualAgiOpenEndTime;

    @Schema(description = "联系方式")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;
}