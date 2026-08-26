package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 11:32
 */
@Data
public class PcImageOciOverviewPageDTO {
    @Schema(description = "类型")
    private String type;

    @Schema(description = "镜像版本，当type=Image")
    private String imageVersion;

    @Schema(description = "网络地址")
    private String networkAddress;

    @Schema(description = "镜像大小")
    private Long imageSize;

    @Schema(description = "镜像摘要")
    private String imageDigest;

    @Schema(description = "操作系统/架构")
    private String osArch;

    @Schema(description = "更新时间")
    private String updateTime;

    @Schema(description = "制品版本，当type=Chart")
    private String artifactVersion;

}
