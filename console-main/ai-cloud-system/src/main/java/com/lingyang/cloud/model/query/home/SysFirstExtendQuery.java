package com.lingyang.cloud.model.query.home;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysFirstExtendQuery {

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 查询 1 一级 2 二级
     */
    @Schema(description = "查询 1 一级 2 二级")
    private Integer type;

    /**
     * 等级（1 新用户，2 激活用户， 3 老用户）
     */
    @Schema(description = "等级（1 新用户，2 激活用户， 3 老用户）")
    private Integer level;

    /**
     * 关联日期开始
     */
    @Schema(description = "关联日期开始")
    private String startTime;

    /**
     * 关联日期结束
     */
    @Schema(description = "关联日期结束")
    private String endTime;



}
