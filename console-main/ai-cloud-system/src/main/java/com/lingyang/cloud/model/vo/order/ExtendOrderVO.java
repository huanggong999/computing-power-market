package com.lingyang.cloud.model.vo.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ExtendOrderVO {


    /**
     * 订单id
     */
    @Schema(description = "订单id")
    private Long id;


    /**
     * 等级（1 新用户，2 激活用户， 3 老用户）
     */
    @Schema(description = "等级（1 新用户，2 激活用户， 3 老用户）")
    private Integer level;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 订单类型
     */
    @Schema(description = "订单类型")
    private OrderTypeEnum orderType;


    /**
     * 最终支付价格
     */
    @Schema(description = "最终支付价格")
    private BigDecimal finalPayAmount;


    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "支付时间")
    private Date payTime;

    /**
     * 订单状态
     */
    @Schema(description = "订单状态")
    private OrderStatusEnum orderStatus;

    /**
     * 一级用户id
     */
    @TableField("first_user_id")
    @Schema(description = "一级用户id")
    private Long firstUserId;

    /**
     * 二级用户id
     */
    @TableField("two_user_id")
    @Schema(description = "二级用户id")
    private Long twoUserId;

    /**
     * 一级佣金比例
     */
    @Schema(description = "一级佣金比例")
    private BigDecimal firstRate;

    /**
     * 二级佣金比例
     */
    @Schema(description = "二级佣金比例")
    private BigDecimal twoRate;

    /**
     * 一级用户佣金
     */
    @Schema(description = "一级用户佣金")
    private BigDecimal firstUserCommission;

    /**
     * 二级用户佣金
     */
    @Schema(description = "二级用户佣金")
    private BigDecimal twoUserCommission;

    /**
     * 用户信息-名称
     */
    @Schema(description = "用户信息-名称")
    private String userName;

    /**
     * 用户信息-手机号
     */
    @Schema(description = "用户信息-手机号")
    private String userPhone;

    /**
     * 一级用户信息-名称
     */
    @Schema(description = "一级用户信息-头像")
    private String firstAvatar;

    /**
     * e人级用户信息-名称
     */
    @Schema(description = "e人级用户信息-头像")
    private String twoAvatar;

    /**
     * 一级用户信息-名称
     */
    @Schema(description = "一级用户信息-名称")
    private String firstUserName;

    /**
     * 一级用户信息-手机号
     */
    @Schema(description = "一级用户信息-手机号")
    private String firstUserPhone;

    /**
     * 二级用户信息-名称
     */
    @Schema(description = "二级用户信息-名称")
    private String twoUserName;

    /**
     * 二级用户信息-手机号
     */
    @Schema(description = "二级用户信息-手机号")
    private String twoUserPhone;

    /**
     * 结算状态 （1 待结算 2 已结算）
     */
    @Schema(description = "结算状态 （1 待结算 2 已结算）")
    private Integer settlementStatus;

    private List<SysOrderSourceEntity> orders;



}
