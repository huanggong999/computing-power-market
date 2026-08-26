package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCustomerVoucherEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 15:17
 */
public interface SysCustomerVoucherService {
    PageResult<SysCustomerVoucherEntity> getPage(PageQuery<SysCustomerVoucherEntity> pageQuery);

    Boolean addCustomerVoucher(SysCustomerVoucherEntity entity);

    BigDecimal getUserVoucherBalance(Long userId);

    Map<Long, BigDecimal> batchUserVoucherBalance(Collection<Long> userIdList);

    void useVoucher(Long orderId,String orderNo, BigDecimal voucherAmount, Long customerId);
}
