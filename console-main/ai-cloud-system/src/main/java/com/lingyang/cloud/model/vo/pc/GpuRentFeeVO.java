package com.lingyang.cloud.model.vo.pc;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GpuRentFeeVO {
    private BigDecimal unitPrice;
    private Integer quantity;
    private Integer duration;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
}
