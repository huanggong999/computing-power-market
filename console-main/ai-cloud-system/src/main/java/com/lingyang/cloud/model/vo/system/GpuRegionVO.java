package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU地区管理VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU地区管理列表项")
public class GpuRegionVO {

    @Schema(description = "地区ID")
    private Long id;

    @Schema(description = "地区编码")
    private String regionCode;

    @Schema(description = "地区名称")
    private String regionName;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "状态 1启用 0禁用")
    private Integer status;
}
