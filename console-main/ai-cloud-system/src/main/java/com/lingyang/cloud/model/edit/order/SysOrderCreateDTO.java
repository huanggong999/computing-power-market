package com.lingyang.cloud.model.edit.order;

import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.model.dto.PcNetworkFormAddDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:32
 */
@Data
public class SysOrderCreateDTO {

    /***
     * 实例id
     */
    private Long id;

    @Schema(description = "订单类型")
    @NotNull(message = "创建订单类型为空")
    private OrderTypeEnum orderType;

    @Schema(description = "订单号，创建续费订单需要")
    private String orderNo;

    @Schema(description = "金额，充值订单需要")
    private BigDecimal amount;

    @Schema(description = "优惠卷id")
    private Long couponId;

    @Schema(description = "代金卷支付")
    private Boolean voucherPay = false;

    @Schema(description = "余额支付")
    private Boolean balancePay = false;

    @Schema(description = "授信额支付")
    private Boolean creditLinePay = false;

    @Schema(description = "在线支付方式")
    private OrderOnlinePayEnum onlinePayType;

    @Schema(description = "订单资源（云服务器，云盘，网络等）")
    private List<SysOrderSourceCreateDTO> orderSource;

    @Schema(description = "网络产品购买记录id")
    private Long networkPayValueId;

    @Schema(description = "网络产品id")
    private Long networkProductId;

    /**
     * 购买产品数量
     */
    @Schema(description = "购买产品数量")
    private Integer networkCount = 1;


    /**
     * 带宽(M) 数量
     */
    @Schema(description = "带宽(M) 数量")
    private Integer bandwidth = 1;

    /**
     * 产品天数
     */
    @Schema(description = "产品天数")
    private Integer networkDay = 1;

    /**
     * 购买IP数量
     */
    @Schema(description = "购买IP数量")
    private Integer ipCount = 1;

    @Schema(description = "支付表单参数")
    private PcNetworkFormAddDTO payFormValue;

    @Schema(description = "是否小程序支付 true 是 ")
    private Boolean isApplet = false;

    private String ip;

    private String loginCode;

    /**
     * 联系方式
     */
    @Schema(description = "联系方式")
    private String mobile;

    /**
     * 联系邮箱
     */
    @Schema(description = "联系邮箱")
    private String email;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "客户名称（购买产品时补充字段）")
    private String agiCustomerName;

    @Schema(description = "是否自动续费（0否 1是）")
    private Integer isAutoRenew;
}