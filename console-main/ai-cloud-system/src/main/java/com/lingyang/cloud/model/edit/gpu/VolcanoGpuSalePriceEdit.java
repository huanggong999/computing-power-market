package com.lingyang.cloud.model.edit.gpu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 火山云 GPU 平台售价编辑参数
 */
@Data
@Schema(description = "火山云GPU平台售价编辑参数")
public class VolcanoGpuSalePriceEdit {

    @Schema(description = "售价配置ID，null=新增")
    private Long id;

    @Schema(description = "火山云地域编码")
    @NotBlank(message = "地域不能为空")
    @Size(max = 64, message = "地域长度不能超过64个字符")
    private String regionCode;

    @Schema(description = "火山云实例规格ID")
    @NotBlank(message = "实例规格不能为空")
    @Size(max = 128, message = "实例规格长度不能超过128个字符")
    private String instanceTypeId;

    @Schema(description = "GPU型号快照")
    @NotBlank(message = "GPU型号不能为空")
    @Size(max = 64, message = "GPU型号长度不能超过64个字符")
    private String gpuModel;

    @Schema(description = "GPU显存快照")
    @Size(max = 32, message = "GPU显存长度不能超过32个字符")
    private String gpuMemory;

    @Schema(description = "实例GPU卡数快照")
    private Integer gpuCount;

    @Schema(description = "计费类型 on_demand/hourly/daily/weekly/monthly")
    @NotBlank(message = "计费类型不能为空")
    private String billingType;

    @Schema(description = "保存时火山实际价快照")
    @DecimalMin(value = "0", message = "火山实际价不能小于0")
    private BigDecimal upstreamPrice;

    @Schema(description = "平台售价")
    @NotNull(message = "平台售价不能为空")
    @DecimalMin(value = "0.0001", message = "平台售价必须大于0")
    private BigDecimal salePrice;

    @Schema(description = "状态 1启用 0停用")
    private Integer status;

    @Schema(description = "备注")
    @Size(max = 256, message = "备注长度不能超过256个字符")
    private String remark;
}
