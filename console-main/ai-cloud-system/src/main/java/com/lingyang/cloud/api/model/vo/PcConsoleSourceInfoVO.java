package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 18:05
 */
@Data
public class PcConsoleSourceInfoVO {

    @Schema(description = "服务器运行中数量")
    private Long ecsRunningNumber = 0L;

    @Schema(description = "服务器已停止数量")
    private Long ecsStoppedNumber = 0L;

    @Schema(description = "即将到期数量")
    private Long expiringNumber = 0L;

    @Schema(description = "已到期数量")
    private Long expireNumber = 0L;

    @Schema(description = "地域资源信息统计")
    private List<PcSourceRegionsNumberInfoVO>  regionsNumberList;
}
