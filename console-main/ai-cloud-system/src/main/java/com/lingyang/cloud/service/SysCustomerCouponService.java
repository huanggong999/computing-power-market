package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.model.query.coupon.SysCustomerCouponQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerCouponListVO;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/24 11:17
 */
public interface SysCustomerCouponService {

    /**
     * 领取优惠卷
     * @param userId 用户id
     * @param couponList 优惠卷信息
     * @return 结果
     */
    boolean receiveCoupon(Long userId, List<SysCouponEntity> couponList);

    /**
     * 校验优惠卷领取
     * @param userId 用户id
     * @param couponEntity 优惠卷信息
     */
    void checkCouponReceive(Long userId, SysCouponEntity couponEntity);
    /**
     * 获取客户优惠卷列表
     * @param query 优惠卷实体
     * @return 优惠卷列表
     */
    List<SysCustomerCouponListVO> getList(SysCustomerCouponQuery query);

}
