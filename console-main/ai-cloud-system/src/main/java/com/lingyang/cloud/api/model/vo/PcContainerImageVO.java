package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 16:25
 */
@Data
public class PcContainerImageVO {

    @Schema(description = "操作系统类型（veLinux，Ubuntu）")
    private String type;

    @Schema(description = "实例规格")
    private String instanceTypeId;
}
