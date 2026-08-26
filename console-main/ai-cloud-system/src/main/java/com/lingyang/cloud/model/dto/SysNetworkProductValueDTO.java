package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.model.vo.product.SysOpenProductUserPwdVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/5 15:26
 */
@Data
public class SysNetworkProductValueDTO {

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "记录id")
    private Long id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品状态（1 未开通， 2 已开通， 3 已过期）")
    private Integer actualStatus;

    /**
     * 计费类型：按量计费（后付费），包年包月（先付费）
     */
    @Schema(description = "计费类型：按量计费（后付费），包年包月（先付费）")
    private SourceChargeTypeEnum chargeType;

    /**
     * 时长
     */
    @Schema(description = "时长")
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @Schema(description = "时长单位")
    private SourceChargeUnitEnum durationUnit;

    @Schema(description = "产品到期时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualAgiExpireTime;

    /**
     * 购买产品数量
     */
    @Schema(description = "购买产品数量")
    private Integer networkCount;

    /**
     * 购买IP数量
     */
    @Schema(description = "购买IP数量")
    @TableField("ip_count")
    private Integer ipCount;

    /**
     * 带宽(M) 数量
     */
    @Schema(description = "带宽(M) 数量")
    private Integer bandwidth;

    @Schema(description = "联系方式")
    private String mobile;

    @Schema(description = "联系邮箱集合")
    private List<String> emails;

    @Schema(description = "账密集合")
    private List<SysOpenProductUserPwdVO> userPwdList;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "支付时间")
    private Date payTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开通时间")
    private Date actualAgiOpenTime;

    @Schema(description = "是否自动续费（0否 1是）")
    private Integer isAutoRenew;

    @Schema(description = "客户名称（购买产品时补充字段）")
    private String agiCustomerName;

    @Schema(description = "IP地区")
    private String ipAddress;

    @Schema(description = "账户、IP是否绑定(0否 1是)")
    private Integer isAccountIpBinding;

    @Schema(description = "是否显示IP(0否 1显示)")
    private Integer isIpDisplay;

    @Schema(description = "是否显示带宽(0否 1显示)")
    private Integer isBandwidthDisplay;

    @Schema(description = "备注")
    private String remark;
}
