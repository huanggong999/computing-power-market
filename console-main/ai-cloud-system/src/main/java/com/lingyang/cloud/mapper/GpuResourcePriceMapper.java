package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.GpuResourcePriceEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GPU资源价格Mapper
 * @author Claude
 * @Date: 2025/05/13
 */
@Mapper
public interface GpuResourcePriceMapper extends CustomMapper<GpuResourcePriceEntity> {

    /**
     * 根据资源ID和计费方式查询价格
     */
    GpuResourcePriceEntity selectByResourceAndBilling(@Param("resourceId") Long resourceId,
                                                       @Param("billingType") String billingType);

    /**
     * 根据资源ID查询所有价格
     */
    List<GpuResourcePriceEntity> selectByResourceId(@Param("resourceId") Long resourceId);
}
