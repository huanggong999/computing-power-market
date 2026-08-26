package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.GpuResourceStockEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * GPU资源库存Mapper
 * @author Claude
 * @Date: 2025/05/13
 */
@Mapper
public interface GpuResourceStockMapper extends CustomMapper<GpuResourceStockEntity> {

    /**
     * 根据资源ID查询库存
     */
    GpuResourceStockEntity selectByResourceId(@Param("resourceId") Long resourceId);
}
