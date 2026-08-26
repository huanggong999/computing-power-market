package com.lingyang.cloud.api.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/18 14:07
 */
@Data
public class PcImageOciOverviewQuery {
    @Schema(description = "实例名称")
    private String instanceName;

    @Schema(description = "镜像版本名称")
    private String imageVersionName;

    @Schema(description = "当前页")
    private Long pageNo;

    @Schema(description = "每页条数")
    private Long pageSize;

    @Schema(description = "类型：Image、Chart")
    private String type;
}
