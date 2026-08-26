package com.lingyang.cloud.model.query.module;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysModuleQuery {

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 发布者名称
     */
    @Schema(description = "发布者名称")
    private String publishUserName;

    /**
     * 类型（1 模型， 2 数据）
     */
    @Schema(description = "类型（1 模型， 2 数据）")
    private Integer type;

    private Integer status;

}
