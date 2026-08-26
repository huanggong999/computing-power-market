package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * GPU资源详情VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU资源详情")
public class GpuResourceDetailVO {

    @Schema(description = "资源ID")
    private Long id;

    @Schema(description = "资源ID")
    private Long resourceId;

    @Schema(description = "资源编号")
    private String resourceNo;

    @Schema(description = "GPU型号")
    private String model;

    @Schema(description = "显存")
    private String vram;

    @Schema(description = "地区名称")
    private String region;

    @Schema(description = "地区编码")
    private String regionCode;

    @Schema(description = "规格ID")
    private Long specId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "专区名称")
    private String zone;

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "来源GPU集群ID")
    private String clusterId;

    @Schema(description = "来源GPU集群名称")
    private String clusterName;

    @Schema(description = "来源GPU集群节点名称")
    private String clusterNodeName;

    @Schema(description = "机器编号")
    private String machineId;

    @Schema(description = "机器UUID")
    private String machineUuid;

    @Schema(description = "可租用至")
    private String rentableUntil;

    @Schema(description = "空闲数量")
    private Integer availableCount;

    @Schema(description = "总量")
    private Integer totalCount;

    @Schema(description = "是否缓存优化")
    private Boolean cacheOptimized;

    @Schema(description = "GPU数量")
    private Integer gpuCount;

    @Schema(description = "GPU驱动版本")
    private String gpuDriver;

    @Schema(description = "CUDA版本")
    private String cudaVersion;

    @Schema(description = "CPU核心数")
    private Integer cpuCores;

    @Schema(description = "CPU型号")
    private String cpuModel;

    @Schema(description = "内存")
    private String memory;

    @Schema(description = "内存")
    private String memorySize;

    @Schema(description = "系统盘")
    private String systemDisk;

    @Schema(description = "数据盘")
    private String dataDisk;

    @Schema(description = "可扩容")
    private String expandable;

    @Schema(description = "价格列表")
    private List<GpuPriceItemVO> prices;

    @Schema(description = "组件列表")
    private List<GpuComponentItemVO> components;

    /**
     * 价格项
     */
    @Data
    @Schema(description = "价格项")
    public static class GpuPriceItemVO {

        @Schema(description = "计费方式")
        private String billingType;

        @Schema(description = "计费方式名称")
        private String billingTypeName;

        @Schema(description = "单价")
        private String unitPrice;

        @Schema(description = "折扣价")
        private String discountPrice;

        @Schema(description = "折扣率")
        private String discountRate;
    }

    @Data
    @Schema(description = "组件项")
    public static class GpuComponentItemVO {

        @Schema(description = "组件ID")
        private Long componentId;

        @Schema(description = "组件名称")
        private String componentName;

        @Schema(description = "基础镜像")
        private String baseImage;

        @Schema(description = "镜像地址")
        private String imageAddress;

        @Schema(description = "排序")
        private Integer sortOrder;
    }
}
