package com.lingyang.cloud.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 火山云 GPU 平台售价配置
 */
@Data
@Schema(description = "火山云GPU平台售价配置")
public class VolcanoGpuSalePriceVO {

    @Schema(description = "售价配置ID")
    private Long id;

    @Schema(description = "火山云地域编码")
    private String regionCode;

    @Schema(description = "火山云实例规格ID")
    private String instanceTypeId;

    @Schema(description = "GPU型号快照")
    private String gpuModel;

    @Schema(description = "GPU显存快照")
    private String gpuMemory;

    @Schema(description = "实例GPU卡数快照")
    private Integer gpuCount;

    @Schema(description = "计费类型 on_demand/hourly/daily/weekly/monthly")
    private String billingType;

    @Schema(description = "保存时火山实际价快照")
    private BigDecimal upstreamPrice;

    @Schema(description = "平台售价")
    private BigDecimal salePrice;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "状态 1启用 0停用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
