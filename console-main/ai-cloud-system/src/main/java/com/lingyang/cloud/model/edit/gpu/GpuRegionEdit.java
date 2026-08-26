package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * GPU地区编辑参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU地区编辑参数")
public class GpuRegionEdit {

    @Schema(description = "地区ID，null=新增")
    private Long id;

    @Schema(description = "地区编码")
    @NotBlank(message = "地区编码不能为空")
    private String regionCode;

    @Schema(description = "地区名称")
    @NotBlank(message = "地区名称不能为空")
    private String regionName;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
