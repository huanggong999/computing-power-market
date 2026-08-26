package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.model.query.customer.SysCustomerBillQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 10:37
 */
public interface SysCustomerBillService {
    PageResult<SysCustomerBillOverviewVO> getBillOverviewPage(PageQuery<SysCustomerBillQuery> pageQuery);

    PageResult<SysCustomerBillEntity> getBillPage(PageQuery<SysCustomerBillQuery> pageQuery);

    void handlerCustomerBill(Long userId);

    BigDecimal getArrearsAmount(Long userId);

    void handlerCustomerBillProduct(Long valueOf);

    void handlerCustomerBillSelfBuild(Long valueOf);
}
