package com.lingyang.cloud.api.model.vo;

import com.lingyang.cloud.enums.source.EcsStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 16:25
 */
@Data
public class PcContainerListVO {
    @Schema(description = "容器名称")
    private String clusterName;
    @Schema(description = "容器状态")
    private EcsStatusEnum status;
    @Schema(description = "用户id")
    private Long customerId;
}
