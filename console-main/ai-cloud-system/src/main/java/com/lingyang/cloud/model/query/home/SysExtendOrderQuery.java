package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysExtendOrderQuery {

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String name;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private String startTime;
    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private String endTime;


    /**
     * 等级（1 新用户，2 激活用户， 3 老用户）
     */
    @Schema(description = "等级（1 新用户，2 激活用户， 3 老用户）")
    @TableField("level")
    private Integer level;


    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 结算状态 （1 待结算 2 已结算）
     */
    @Schema(description = "结算状态 （1 待结算 2 已结算）")
    @TableField("settlement_status")
    private Integer settlementStatus;


}
