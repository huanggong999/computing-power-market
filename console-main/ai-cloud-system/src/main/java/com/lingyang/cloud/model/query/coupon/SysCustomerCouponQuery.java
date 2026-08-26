package com.lingyang.cloud.model.query.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 15:01
 */
@Data
public class SysCustomerCouponQuery {

    @Schema(description = "客户优惠卷id")
    private Long id;
    /**
     * 优惠卷id
     */
    @Schema(description = "优惠卷id")
    private Long couponId;

    @Schema(description = "优惠卷id列表查询")
    private Collection<Long> couponIds;

    /**
     * 客户id
     */
    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "资源列表")
    private List<SysCustomerCouponSourceQuery> sourceList;

    @Schema(hidden = true)
    private Map<Integer, BigDecimal> priceMap;
}