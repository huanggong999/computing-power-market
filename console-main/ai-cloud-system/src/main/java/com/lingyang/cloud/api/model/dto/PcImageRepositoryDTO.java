package com.lingyang.cloud.api.model.dto;

import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 11:32
 */
@Data
public class PcImageRepositoryDTO {
    /**
     * 实例名称
     */
    @Schema(description = "实例名称")
    private String instanceName;

    @Schema(description = "系统域名")
    private String domain;

    @Schema(description = "状态")
    private ContainerStatusEnum status;

    @Schema(description = "username")
    private String username;

    @Schema(description = "Project")
    private String project;

    @Schema(description = "地域")
    private String region;

    @Schema(description = "计费类型")
    private SourceChargeTypeEnum chargeType;

    @Schema(description = "版本")
    private String version;



}
