package com.lingyang.cloud.model.query.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU资源查询参数
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU资源查询参数")
public class GpuResourceQuery {

    @Schema(description = "资源编号")
    private String resourceNo;

    @Schema(description = "GPU型号")
    private String model;

    @Schema(description = "规格ID")
    private Long specId;

    @Schema(description = "地区编码")
    private String regionCode;

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "机器编号")
    private String machineId;

    @Schema(description = "状态 1上架 2下架 3维护中")
    private Integer status;
}
