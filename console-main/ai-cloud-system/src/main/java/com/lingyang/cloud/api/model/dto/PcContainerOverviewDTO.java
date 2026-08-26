package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 16:14
 */
@Data
public class PcContainerOverviewDTO {

    @Schema(description = "集群总数")
    private Integer containerTotal = 0;
    @Schema(description = "集群正常数量")
    private Integer containerNormalNumber = 0;
    @Schema(description = "集群异常数量")
    private Integer containerAbnormalNumber = 0;
    @Schema(description = "集群其他数量")
    private Integer containerOtherNumber = 0;
    @Schema(description = "节点总数")
    private Integer nodePoolTotal = 0;
    @Schema(description = "节点正常数量")
    private Integer nodePoolNormalNumber = 0;
    @Schema(description = "节点异常数量")
    private Integer nodePoolAbnormalNumber = 0;
    @Schema(description = "节点其他数量")
    private Integer nodePoolOtherNumber = 0;
    @Schema(description = "集群列表")
    private List<PcContainerListDTO> pcContainerList;

}
