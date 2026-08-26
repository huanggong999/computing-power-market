package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.GpuComponentEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * GPU基础组件镜像Mapper
 */
@Mapper
public interface GpuComponentMapper extends CustomMapper<GpuComponentEntity> {
}
