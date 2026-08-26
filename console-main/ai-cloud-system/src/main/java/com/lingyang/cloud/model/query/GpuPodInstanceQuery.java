package com.lingyang.cloud.model.query;

import com.lingyang.common.core.model.page.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "GPU Pod实例查询参数")
public class GpuPodInstanceQuery extends PageQuery {

    @Schema(description = "实例名称（模糊查询）")
    private String name;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "区域编码")
    private String regionCode;

    @Schema(description = "GPU型号")
    private String gpuModel;

    @Schema(description = "计费模式")
    private String billingMode;
}
