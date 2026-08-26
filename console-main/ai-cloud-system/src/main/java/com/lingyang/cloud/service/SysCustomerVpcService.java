package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCustomerVpcEntity;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.volcengine.ApiException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 20:13
 */
public interface SysCustomerVpcService {


    SysCustomerVpcEntity getCustomerVpcOnInsert(Long customerId, SourceRegionsEnum regions) throws ApiException;
}
