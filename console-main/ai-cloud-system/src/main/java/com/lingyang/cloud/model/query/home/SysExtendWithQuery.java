package com.lingyang.cloud.model.query.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysExtendWithQuery {

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String name;

    /**
     * 提交开始时间
     */
    @Schema(description = "提交开始时间")
    private String startTime;
    /**
     * 提交结束时间
     */
    @Schema(description = "提交结束时间")
    private String endTime;


    /**
     * 状态（1审核中，2通过，3不通过, 4 已打款）
     */
    @Schema(description = "状态（1审核中，2通过，3不通过 4 已打款）")
    private Integer status;


    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;


}
