package com.lingyang.cloud.model.vo.customer;

import com.lingyang.cloud.entity.SysCustomerDiscountEntity;
import com.lingyang.cloud.entity.SysCustomerNetwork;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.web.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:05
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysCustomerListVO extends BaseVO {
    @Serial
    private static final long serialVersionUID = -676157857213513025L;

    @Schema(description = "账号id")
    private Long accountId;

    /**
     * 账号密钥key
     */
    @Schema(description = "账号密钥key")
    private String accountKey;

    /**
     * 客户名称
     */
    @Schema(description = "客户名称")
    private String customerName;

    /**
     * 手机号-登陆账号
     */
    @Schema(description = "手机号-登陆账号")
    private String phone;

    @Schema(description = "用户上级名称")
    private String pName;

    @Schema(description = "用户上级手机号")
    private String pPhone;

    /**
     * 客户头像
     */
    @Schema(description = "客户头像")
    private String avatar;

    /**
     * 余额
     */
    @Schema(description = "余额")
    private BigDecimal balance;

    /**
     * 代金卷余额
     */
    @Schema(description = "代金卷余额")
    private BigDecimal voucherAmount;

    /**
     * 信用额
     */
    @Schema(description = "信用额")
    private BigDecimal creditAmount;

    /**
     * 累计消费金额
     */
    @Schema(description = "累计消费金额")
    private BigDecimal totalConsumeAmount;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "实例总数")
    private Long instanceTotal;

    @Schema(description = "运行中实例数量")
    private Long instanceRunNumber;

    @Schema(description = "折扣比列列表")
    private List<SysCustomerDiscountEntity> discountList;

    @Schema(description = "AGIC折扣列表")
    private List<SysCustomerNetwork> customerNetworkList;

    private Boolean extend = false;
}
