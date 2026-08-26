package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_extend_config")
public class SysExtendConfig extends BaseEntity {

    /**
     * 二维码
     */
    @TableField("qr_code")
    @Schema(description = "二维码")
    private String qrCode;

    /**
     * 提现手续费比例
     */
    @TableField("with_service_scale")
    @Schema(description = "提现手续费比例")
    private BigDecimal withServiceScale;

    /**
     * 介绍
     */
    @TableField("introduce")
    @Schema(description = "介绍")
    private String introduce;

    /**
     * 一级分佣比例
     */
    @TableField("first_scale")
    @Schema(description = "一级分佣比例")
    private BigDecimal firstScale;

    /**
     * 二级分佣比例
     */
    @TableField("two_scale")
    @Schema(description = "二级分佣比例")
    private BigDecimal twoScale;

}
