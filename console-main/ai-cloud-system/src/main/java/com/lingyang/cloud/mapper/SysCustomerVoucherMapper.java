package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerVoucherEntity;
import com.lingyang.common.datasource.model.CustomMapper;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:17
 */
public interface SysCustomerVoucherMapper extends CustomMapper<SysCustomerVoucherEntity> {

    int returnVoucherAmount(List<SysCustomerVoucherEntity> sysCustomerVoucherList);
}
