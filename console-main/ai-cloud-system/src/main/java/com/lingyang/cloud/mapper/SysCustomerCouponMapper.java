package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerCouponEntity;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;
import com.lingyang.common.datasource.model.CustomMapper;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 11:17
 */
public interface SysCustomerCouponMapper extends CustomMapper<SysCustomerCouponEntity> {
    List<SysCustomerCouponListVO> getList(SysCustomerCouponQuery query);
}
