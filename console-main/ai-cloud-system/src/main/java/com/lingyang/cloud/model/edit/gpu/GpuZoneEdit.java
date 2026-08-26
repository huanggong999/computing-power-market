package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * GPU专区编辑参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU专区编辑参数")
public class GpuZoneEdit {

    @Schema(description = "专区ID，null=新增")
    private Long id;

    @Schema(description = "专区编码")
    @NotBlank(message = "专区编码不能为空")
    private String zoneCode;

    @Schema(description = "专区名称")
    @NotBlank(message = "专区名称不能为空")
    private String zoneName;

    @Schema(description = "所属地区编码")
    private String regionCode;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
