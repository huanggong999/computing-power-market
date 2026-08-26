package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU型号统计VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU型号统计")
public class GpuModelStatVO {

    @Schema(description = "GPU型号，如 RTX 5090")
    private String model;

    @Schema(description = "该型号可租总数")
    private Integer availableCount;

    @Schema(description = "该型号总数量")
    private Integer totalCount;
}
