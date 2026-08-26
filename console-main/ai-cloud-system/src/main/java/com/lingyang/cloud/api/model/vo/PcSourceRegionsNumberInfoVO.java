package com.lingyang.cloud.api.model.vo;

import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:13
 */
@Data
public class PcSourceRegionsNumberInfoVO {

    @Schema(description = "地区")
    private SourceRegionsEnum region;

    @Schema(description = "云服务器数量")
    private Long ecsNumber = 0L;

    @Schema(description = "服务器运行中数量")
    private Long ecsRunningNumber = 0L;

    @Schema(description = "服务器已停止数量")
    private Long ecsStoppedNumber = 0L;

    @Schema(description = "云盘数量")
    private Long cloudStorageNumber = 0L;

    @Schema(description = "镜像数量")
    private Long imgNumber = 0L;
}
