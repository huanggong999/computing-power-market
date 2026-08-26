package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 15:11
 */
@Data
public class PcConsoleHomeVO {
    @Schema(description = "服务器数量")
    private Long ecsNumber;

    @Schema(description = "容器数量")
    private Long containerNumber = 0L;

    @Schema(description = "对象存储数量")
    private Long objectStorageNumber = 0L;

    @Schema(description = "镜像数量")
    private Long imgNumber = 0L;

}
