package com.lingyang.cloud.model.query.apply;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.common.core.model.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SysApplyQuery  {

    /**
     * 分类id
     */
    @Schema(description = "分类id")
    private Long typeId;

    /**
     * 应用名称
     */
    @Schema(description = "应用名称")
    private String name;

    /**
     * 发布者名称
     */
    @Schema(description = "发布者名称")
    private String publishUserName;

    /**
     * 状态
     */
    @Schema(description = "状态")
    private Integer status;

}
