package com.lingyang.cloud.model.vo.pc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * GPU市场筛选条件元数据VO
 * @author Claude
 * @Date: 2025/05/13
 */
@Data
@Schema(description = "GPU市场筛选条件元数据")
public class GpuMarketMetaVO {

    @Schema(description = "地区列表")
    private List<GpuRegionItemVO> regions;

    @Schema(description = "专区列表")
    private List<GpuZoneItemVO> zones;

    @Schema(description = "GPU型号及统计")
    private List<GpuModelStatVO> gpuModels;

    @Schema(description = "GPU数量选项")
    private List<Integer> gpuCounts;

    @Schema(description = "计费方式列表")
    private List<GpuBillingTypeVO> billingTypes;
}
