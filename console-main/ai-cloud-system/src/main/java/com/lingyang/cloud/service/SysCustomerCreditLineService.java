package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:02
 */

public interface SysCustomerCreditLineService  {
    /**
     * 新增客户授信额度
     * @param entity
     * @return
     */
    Boolean addCustomerCreditLine(SysCustomerCreditLineEntity entity);

    /**
     * 查询客户授信额度
     * @param userId
     * @return
     */
    BigDecimal getCreditAmount(Long userId);

    /**
     * 分页查询客户授信额度
     * @param pageQuery
     * @return
     */
    PageResult<SysCustomerCreditLineEntity> getPage(PageQuery<SysCustomerCreditLineEntity> pageQuery);

    /**
     * 批量查询客户授信额度
     * @param customerIdSet
     * @return
     */
    Map<Long, BigDecimal> batchUserCreditLine(Set<Long> customerIdSet);

    /**
     * 使用授信额度
     * @param id
     * @param billNo
     * @param payPrice
     * @param customerId
     */
    void useCreditLine(Long id, String billNo, BigDecimal payPrice, Long customerId);
}
