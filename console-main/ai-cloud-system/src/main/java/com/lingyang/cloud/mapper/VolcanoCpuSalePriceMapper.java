package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.VolcanoCpuSalePriceEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VolcanoCpuSalePriceMapper extends CustomMapper<VolcanoCpuSalePriceEntity> {
    VolcanoCpuSalePriceEntity selectByUniqueKey(@Param("regionCode") String regionCode, @Param("instanceTypeId") String instanceTypeId, @Param("billingType") String billingType);
    int deletePhysicalById(@Param("id") Long id);
}
