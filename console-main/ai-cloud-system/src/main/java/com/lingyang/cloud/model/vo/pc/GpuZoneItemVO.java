package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU专区项VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU专区项")
public class GpuZoneItemVO {

    @Schema(description = "专区编码")
    private String zoneCode;

    @Schema(description = "专区名称")
    private String zoneName;
}
