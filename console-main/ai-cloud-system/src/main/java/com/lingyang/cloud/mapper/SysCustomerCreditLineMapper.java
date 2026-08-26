package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.common.datasource.model.CustomMapper;

import java.util.List;

/**
 * @author 吴思镇
 */
public interface SysCustomerCreditLineMapper extends CustomMapper<SysCustomerCreditLineEntity> {

    int returnCreditLineAmount(List<SysCustomerCreditLineEntity> sysCustomerVoucherList);
}
