package com.lingyang.cloud.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuResourceComponentEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * GPU资源组件镜像关联Mapper
 */
@Mapper
public interface GpuResourceComponentMapper extends CustomMapper<GpuResourceComponentEntity> {

    default List<GpuResourceComponentEntity> selectByResourceId(Long resourceId) {
        LambdaQueryWrapper<GpuResourceComponentEntity> wrapper = Wrappers.lambdaQuery(GpuResourceComponentEntity.class)
                .eq(GpuResourceComponentEntity::getResourceId, resourceId)
                .eq(GpuResourceComponentEntity::getDelFlag, 0)
                .orderByAsc(GpuResourceComponentEntity::getSortOrder)
                .orderByAsc(GpuResourceComponentEntity::getId);
        return selectList(wrapper);
    }

    default void deleteByResourceId(Long resourceId) {
        LambdaQueryWrapper<GpuResourceComponentEntity> wrapper = Wrappers.lambdaQuery(GpuResourceComponentEntity.class)
                .eq(GpuResourceComponentEntity::getResourceId, resourceId);
        delete(wrapper);
    }
}
