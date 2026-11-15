package com.lingyang.cloud.model.vo.system;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class VolcanoCpuSalePriceVO {
    private Long id;
    private String regionCode;
    private String instanceTypeId;
    private String family;
    private Integer cpuCores;
    private String cpuModel;
    private BigDecimal memGib;
    private String billingType;
    private BigDecimal upstreamPrice;
    private BigDecimal salePrice;
    private BigDecimal premium;
    private String currency;
    private Integer status;
    private String remark;
    private String priceVersion;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
