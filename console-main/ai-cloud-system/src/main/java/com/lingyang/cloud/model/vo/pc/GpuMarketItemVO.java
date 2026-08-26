package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU市场列表项VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU市场列表项")
public class GpuMarketItemVO {

    @Schema(description = "资源ID")
    private Long resourceId;

    @Schema(description = "资源编号")
    private String resourceNo;

    @Schema(description = "GPU型号，如 RTX 5090")
    private String model;

    @Schema(description = "显存，如 32 GB")
    private String vram;

    @Schema(description = "地区名称，如 西北B区")
    private String region;

    @Schema(description = "地区编码")
    private String regionCode;

    @Schema(description = "专区名称，如 4090专区")
    private String zone;

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "机器编号，如 D21机")
    private String machineId;

    @Schema(description = "机器UUID，如 3y80s00ajm")
    private String machineUuid;

    @Schema(description = "可租用至，如 2027-05-01")
    private String rentableUntil;

    @Schema(description = "空闲数量")
    private Integer availableCount;

    @Schema(description = "总量")
    private Integer totalCount;

    @Schema(description = "是否缓存优化")
    private Boolean cacheOptimized;

    @Schema(description = "CPU核心数")
    private Integer cpuCores;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "内存")
    private String memory;

    @Schema(description = "系统盘")
    private String systemDisk;

    @Schema(description = "数据盘")
    private String dataDisk;

    @Schema(description = "可扩容")
    private String expandable;

    @Schema(description = "GPU驱动版本")
    private String gpuDriver;

    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @Schema(description = "时价，如 3.03")
    private String price;

    @Schema(description = "折扣价，如 2.88")
    private String discountPrice;

    @Schema(description = "折扣率，如 9.5")
    private String discountRate;

    @Schema(description = "可租数量（前端按钮显示）")
    private Integer rentableCount;
}
