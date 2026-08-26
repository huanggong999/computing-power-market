package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysExtendQuery {

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    private String name;
    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String phone;

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
     * 状态（1审核中，2通过，3不通过）
     */
    @Schema(description = "状态（1审核中，2通过，3不通过）")
    private Integer status;


}
