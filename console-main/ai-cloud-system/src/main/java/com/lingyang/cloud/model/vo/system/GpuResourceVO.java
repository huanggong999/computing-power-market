package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU资源管理列表VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU资源管理列表项")
public class GpuResourceVO {

    @Schema(description = "资源ID")
    private Long id;

    @Schema(description = "资源编号")
    private String resourceNo;

    @Schema(description = "机器编号")
    private String machineId;

    @Schema(description = "机器UUID")
    private String machineUuid;

    @Schema(description = "GPU型号")
    private String model;

    @Schema(description = "显存")
    private String vram;

    @Schema(description = "地区名称")
    private String regionName;

    @Schema(description = "专区名称")
    private String zoneName;

    @Schema(description = "CPU核心数")
    private Integer cpuCores;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "内存")
    private String memorySize;

    @Schema(description = "空闲数量")
    private Integer availableCount;

    @Schema(description = "总量")
    private Integer totalCount;

    @Schema(description = "时价")
    private String price;

    @Schema(description = "折扣价")
    private String discountPrice;

    @Schema(description = "可租至")
    private String rentableUntil;

    @Schema(description = "状态 1上架 2下架 3维护中")
    private Integer status;
}
