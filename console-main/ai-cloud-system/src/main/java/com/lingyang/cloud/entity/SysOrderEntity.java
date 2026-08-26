package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.order.OrderOnlinePayEnum;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024-11-12
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_order")
public class SysOrderEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8936308555076620060L;

    /**
     * 订单号
     */
    @TableField("order_no")
    @Schema(description = "订单号")
    private String orderNo;

    @TableField(exist = false)
    @Schema(description = "客户名称")
    private String customerName;

    @TableField(exist = false)
    @Schema(description = "客户手机号")
    private String customerPhone;

    /**
     * 订单类型
     */
    @TableField("order_type")
    @Schema(description = "订单类型")
    private OrderTypeEnum orderType;

    /**
     * 订单原价
     */
    @TableField("original_price")
    @Schema(description = "订单原价")
    private BigDecimal originalPrice;

    /**
     * 溢价价格
     */
    @TableField("premium_price")
    @Schema(description = "溢价价格")
    private BigDecimal premiumPrice;

    /**
     * 用户折扣金额
     */
    @TableField("user_discount_amount")
    @Schema(description = "用户折扣金额")
    private BigDecimal userDiscountAmount;

    /**
     * 代金券id
     */
    @TableField("voucher_id")
    @Schema(description = "代金券id")
    private Long voucherId;

    /**
     * 代金券金额
     */
    @TableField("voucher_amount")
    @Schema(description = "代金券金额")
    private BigDecimal voucherAmount;

    /**
     * 优惠卷id
     */
    @TableField("coupon_id")
    @Schema(description = "优惠卷id")
    private Long couponId;

    /**
     * 优惠卷金额
     */
    @TableField("coupon_amount")
    @Schema(description = "优惠卷金额")
    private BigDecimal couponAmount;

    /**
     * 授信额id
     */
    @TableField("credit_line_id")
    @Schema(description = "授信额id")
    private Long creditLineId;

    /**
     * 授信额金额
     */
    @TableField("credit_line_amount")
    @Schema(description = "授信额金额")
    private BigDecimal creditLineAmount;

    /**
     * 最终支付价格
     */
    @TableField("final_pay_amount")
    @Schema(description = "最终支付价格")
    private BigDecimal finalPayAmount;

    /**
     * 余额支付金额
     */
    @TableField("balance_pay_amount")
    @Schema(description = "余额支付金额")
    private BigDecimal balancePayAmount;

    /**
     * 在线支付金额
     */
    @TableField("online_pay_amount")
    @Schema(description = "在线支付金额")
    private BigDecimal onlinePayAmount;

    /**
     * 在线支付方式
     */
    @TableField("online_pay_type")
    @Schema(description = "在线支付方式")
    private OrderOnlinePayEnum onlinePayType;

    /**
     * 在线支付流水号
     */
    @TableField("online_pay_serial_number")
    @Schema(description = "在线支付流水号")
    private String onlinePaySerialNumber;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "支付时间")
    private Date payTime;

    /**
     * 订单状态
     */
    @TableField("order_status")
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
    @TableField("first_rate")
    @Schema(description = "一级佣金比例")
    private BigDecimal firstRate;

    /**
     * 二级佣金比例
     */
    @TableField("two_rate")
    @Schema(description = "二级佣金比例")
    private BigDecimal twoRate;

    /**
     * 一级用户佣金
     */
    @TableField("first_user_commission")
    @Schema(description = "一级用户佣金")
    private BigDecimal firstUserCommission;

    /**
     * 二级用户佣金
     */
    @TableField("two_user_commission")
    @Schema(description = "二级用户佣金")
    private BigDecimal twoUserCommission;


    /**
     * 奖励类型 1充值奖励
     */
    @TableField("rewards_type")
    @Schema(description = "奖励类型 1充值奖励")
    private Integer rewardsType;

    /**
     * 充值活动名称
     */
    @TableField("recharge_name")
    @Schema(description = "充值活动名称")
    private String rechargeName;

    /**
     * 充值活动id
     */
    @TableField("recharge_id")
    @Schema(description = "充值活动id")
    private Long rechargeId;

    /**
     * 充值奖励金额
     */
    @TableField("rewards_amount")
    @Schema(description = "充值奖励金额")
    private BigDecimal rewardsAmount;

    /**
     * 网络产品id
     */
    @TableField("network_product_id")
    @Schema(description = "网络产品id")
    private Long networkProductId;



    /**
     * 奖励时间
     */
    @TableField("rewards_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "奖励时间")
    private Date rewardsTime;


    /**
     * 实际产品开通时间
     */
    @TableField("actual_agi_open_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "实际产品开通时间")
    private Date actualAgiOpenTime;

    /**
     * 产品到期时间
     */
    @TableField("actual_agi_expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "产品到期时间")
    private Date actualAgiExpireTime;

    /**
     * 产品状态（1 未开通， 2 已开通， 3 已过期）
     */
    @TableField("actual_status")
    @Schema(description = "产品状态（1 未开通， 2 已开通， 3 已过期）")
    private Integer actualStatus;

    /**
     * 购买订单表单id
     */
    @TableField("network_value_id")
    @Schema(description = "购买订单表单id")
    private Long networkValueId;

    /**
     * 带宽(M) 数量
     */
    @Schema(description = "带宽(M) 数量")
    private Integer bandwidth;

    /**
     * 产品天数
     */
    @Schema(description = "产品天数")
    private Integer networkDay;

    /**
     * 购买产品数量
     */
    @Schema(description = "购买产品数量")
    private Integer networkCount;

    @TableField("email")
    @Schema(description = "联系邮箱")
    private String email;

    @Schema(description = "购买IP数量")
    @TableField("ip_count")
    private Integer ipCount;


    @Schema(description = "记录当前用户带宽(M) 总量")
    @TableField("bandwidth_all")
    private Integer bandwidthAll;

    @Schema(description = "记录当前用户购买产品总量")
    @TableField("network_count_all")
    private Integer networkCountAll;

    @TableField("ip_count_all")
    @Schema(description = "记录当前用户AGIC升级产品的IP总量")
    private Integer ipCountAll;

    /**
     * 结算状态 （1 待结算 2 已结算）
     */
    @Schema(description = "结算状态 （1 待结算 2 已结算）")
    private Integer settlementStatus;


    @TableField(exist = false)
    @Schema(description = "订单资源信息")
    private List<SysOrderSourceEntity> orderSourceList;
    /**
     * 按量付费每小时费用
     */
    @TableField(exist = false)
    @Schema(description = "按量付费每小时费用")
    private BigDecimal hoursPrice;

    /**
     * 网络产品名称
     */
    @TableField(exist = false)
    @Schema(description = "网络产品名称")
    private String networkProductName;

    /**
     * 网络产品JSON
     */
    @TableField(exist = false)
    @Schema(description = "网络产品JSON")
    private String networkProductJson;

    /**
     * 账号密码
     */
    @TableField(exist = false)
    private String userpwd;

    /**
     * 计费类型：按量计费（后付费），包年包月（先付费）
     */
    @Schema(description = "计费类型：按量计费（后付费），包年包月（先付费）")
    @TableField(exist = false)
    private SourceChargeTypeEnum chargeType;

    /**
     * 时长
     */
    @Schema(description = "时长")
    @TableField(exist = false)
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @TableField(exist = false)
    @Schema(description = "时长单位")
    private SourceChargeUnitEnum durationUnit;

    @TableField(exist = false)
    @Schema(description = "客户名称（购买产品时补充字段）")
    private String agiCustomerName;

    /**
     * 联系方式
     */
    @TableField(exist = false)
    @Schema(description = "联系方式")
    private String mobile;

    @TableField(exist = false)
    @Schema(description = "到期状态（0：1天内到期、 1：1天到期、 2：2天到期、 3：3天到期、4：4天到期、 5：5天到期、 6：6天到期、 7：7天到期）")
    private Integer expireStatus;

    @TableField(exist = false)
    @Schema(description = "是否显示带宽(0否 1显示)")
    private Integer isBandwidthDisplay;

    public static void main(String[] args) {
        Map<String, Object> s = new HashMap<>();
        s.put("1", new BigDecimal("123"));
        s.put("2", new String("1223"));
        for (String s1 : s.keySet()) {
            Object o = s.get(s1);
            if (o instanceof BigDecimal) {
                System.out.println(o.toString());
            } else {
                System.out.println(isNumeric(o.toString()));
            }

        }

    }


    public static boolean isNumeric(String str) {
        // 使用正则表达式匹配数字字符串
        return str.matches("\\d+");
    }

}