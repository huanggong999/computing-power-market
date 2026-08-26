package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.GpuResourceEntity;
import com.lingyang.cloud.model.dto.GpuResourceQueryParam;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GPU资源Mapper
 * @author Claude
 * @Date: 2025/05/13
 */
@Mapper
public interface GpuResourceMapper extends CustomMapper<GpuResourceEntity> {

    /**
     * 查询算力市场列表（关联规格、库存、价格）
     */
    List<GpuResourceEntity> selectGpuMarketList(@Param("query") GpuResourceQueryParam query);

    /**
     * 统计算力市场列表总数
     */
    Long countGpuMarketList(@Param("query") GpuResourceQueryParam query);
}
