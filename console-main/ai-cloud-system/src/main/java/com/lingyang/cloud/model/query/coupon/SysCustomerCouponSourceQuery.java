package com.lingyang.cloud.model.query.coupon;

import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/18 11:31
 */
@Data
public class SysCustomerCouponSourceQuery {

    @Schema(description = "资源类型")
    private SourceTypeEnum sourceType;

    @Schema(description = "价格")
    private BigDecimal price;
}
