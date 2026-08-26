package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 14:12
 */
@Data
@TableName("sys_active_coupon")
public class SysActiveCouponEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6459891529580738182L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    /**
     * 活动id
     */
    @Schema(description = "活动id")
    @NotNull(message = "活动id不能为空")
    @TableField("active_id")
    private Long activeId;

    /**
     * 优惠卷id
     */
    @Schema(description = "优惠卷id")
    @NotNull(message = "优惠卷id不能为空")
    @TableField("coupon_id")
    private Long couponId;

    /**
     * 赠卷类型（1注册送 2注册后达标送）
     */
    @Schema(description = "赠卷类型（1注册送 2注册后达标送）")
    @NotNull(message = "type不能为空")
    @TableField("type")
    private Integer type;
}
