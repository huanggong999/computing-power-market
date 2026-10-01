package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.VolcanoGpuSalePriceEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 火山云 GPU 平台售价 Mapper
 */
@Mapper
public interface VolcanoGpuSalePriceMapper extends CustomMapper<VolcanoGpuSalePriceEntity> {

    VolcanoGpuSalePriceEntity selectByUniqueKey(@Param("regionCode") String regionCode,
                                                @Param("instanceTypeId") String instanceTypeId,
                                                @Param("billingType") String billingType);

    int deletePhysicalById(@Param("id") Long id);
}
