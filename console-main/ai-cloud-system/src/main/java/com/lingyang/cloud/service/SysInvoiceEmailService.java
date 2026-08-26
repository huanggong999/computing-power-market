package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysInvoiceEmailEntity;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/10 16:58
 */
public interface SysInvoiceEmailService {
    /**
     * 设置默认邮箱
     *
     * @param entity
     * @return
     */
    Boolean setDefault(SysInvoiceEmailEntity entity);
}
