package com.lingyang.cloud.model.query.esc;

import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/4 10:57
 */
@Data
public class PcEcsQuery {

    @Schema(description = "地域")
    @NotNull(message = "地域不能为空")
    private SourceRegionsEnum sourceRegions;

    @Schema(description = "服务器类型")
    private EcsTypeEnum ecsTypeEnum;

    @Schema(description = "ecsId")
    private Long ecsId;

    @Schema(description = "vCPU数量")
    private Integer vCpuNumber;

    @Schema(description = "内存大小")
    private Integer memorySize;

    @Schema(description = "实例规格")
    private String ecsScale;

    /**
     * gpu型号
     */
    @Schema(description = "gpu型号")
    private String gpuModel;

    /**
     * gpu内存
     */
    @Schema(description = "gpu内存")
    private String gpuMemory;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;
}
