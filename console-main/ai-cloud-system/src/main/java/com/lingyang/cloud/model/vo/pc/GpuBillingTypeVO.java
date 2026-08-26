package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * GPU计费方式VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU计费方式")
public class GpuBillingTypeVO {

    @Schema(description = "计费方式编码，如 hourly")
    private String code;

    @Schema(description = "计费方式名称，如 按量计费")
    private String name;
}
