package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU地区项VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU地区项")
public class GpuRegionItemVO {

    @Schema(description = "地区编码")
    private String regionCode;

    @Schema(description = "地区名称")
    private String regionName;
}
