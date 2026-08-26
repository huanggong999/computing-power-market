package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysActiveCouponEntity;
import com.lingyang.cloud.model.dto.SysActiveCouponDetailsDTO;
import com.lingyang.common.datasource.model.CustomMapper;

import java.util.List;

/**
 * @author 吴思镇
 */
public interface SysActiveCouponMapper extends CustomMapper<SysActiveCouponEntity> {
    /**
     * 获取优惠券集合
     *
     * @param id
     * @return
     */
    List<SysActiveCouponDetailsDTO> getCoupons(Long id);
}
