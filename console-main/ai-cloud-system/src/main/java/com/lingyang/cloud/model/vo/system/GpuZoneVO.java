package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU专区管理VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU专区管理列表项")
public class GpuZoneVO {

    @Schema(description = "专区ID")
    private Long id;

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "专区名称")
    private String zoneName;

    @Schema(description = "所属地区编码")
    private String regionCode;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
