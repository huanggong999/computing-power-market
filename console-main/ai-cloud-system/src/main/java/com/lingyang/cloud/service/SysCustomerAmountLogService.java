package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCustomerAmountLogEntity;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 18:41
 */
public interface SysCustomerAmountLogService {
    PageResult<SysCustomerAmountLogEntity> getPage(PageQuery<SysCustomerAmountLogEntity> pageQuery);
}