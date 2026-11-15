package com.lingyang.cloud.model.edit.gpu;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class VolcanoCpuSalePriceEdit {
    private Long id;
    @NotBlank @Size(max=64) private String regionCode;
    @NotBlank @Size(max=128) private String instanceTypeId;
    @Size(max=128) private String family;
    private Integer cpuCores;
    @Size(max=128) private String cpuModel;
    private BigDecimal memGib;
    @NotBlank private String billingType;
    private BigDecimal upstreamPrice;
    @NotNull @DecimalMin(value="0.0001") private BigDecimal salePrice;
    private Integer status;
    @Size(max=256) private String remark;
    private String priceVersion;
}
