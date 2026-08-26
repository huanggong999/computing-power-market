package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 10:56
 */
@Data
public class AppletsConsoleListDTO {
    @Schema(description = "服务器总数")
    private Long instanceCount;

    @Schema(description = "服务器运行中数量")
    private Long ecsRunningNumber = 0L;

    @Schema(description = "服务器已停止数量")
    private Long ecsStoppedNumber = 0L;

    @Schema(description = "服务器即将到期数量")
    private Long expiringNumber = 0L;

    @Schema(description = "服务器已到期数量")
    private Long expireNumber = 0L;

    @Schema(description = "集群总数")
    private Integer containerTotal;
    @Schema(description = "集群正常数量")
    private Integer containerNormalNumber = 0;
    @Schema(description = "集群异常数量")
    private Integer containerAbnormalNumber = 0;
    @Schema(description = "集群其他数量")
    private Integer containerOtherNumber = 0;

    @Schema(description ="镜像仓库总数")
    private Integer imageCount;

    @Schema(description ="镜像仓库运行中数量")
    private Integer imageRunningNumber = 0;

    @Schema(description ="镜像仓库已停止数量")
    private Integer imageStoppedNumber = 0;

    @Schema(description ="镜像仓库异常数量")
    private Integer imageErrorNumber = 0;

    @Schema(description ="对象存储桶数量")
    private Integer bucketCount;

    @Schema(description = "桶对象数量")
    private Integer bucketObjectCount = 0;

    @Schema(description = "桶对象总容量")
    private BigDecimal bucketObjectSize = BigDecimal.ZERO;

    @Schema(description = "网络产品数量")
    private Integer networkProductCount;

    @Schema(description = "网络产品已开通数量")
    private Integer networkProductRunningNumber = 0;

    @Schema(description = "网络产品已过期数量")
    private Integer networkProductExpireNumber = 0;
}
