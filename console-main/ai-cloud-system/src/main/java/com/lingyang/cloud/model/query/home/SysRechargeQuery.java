package com.lingyang.cloud.model.query.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysRechargeQuery {

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;
    /**

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
     * 状态（1 启用 2停用）
     */
    @Schema(description = "状态（1 启用 2停用）")
    private Integer status;


}
