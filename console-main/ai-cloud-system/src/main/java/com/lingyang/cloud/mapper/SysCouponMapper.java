package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.query.active.SysActiveCouponQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:03
 */
public interface SysCouponMapper extends CustomMapper<SysCouponEntity> {
    /**
     * 获取优惠券列表
     * @param query
     * @return
     */
    List<SysActiveCouponDTO> getCoupons(@Param("param") SysActiveCouponQuery query);
}
