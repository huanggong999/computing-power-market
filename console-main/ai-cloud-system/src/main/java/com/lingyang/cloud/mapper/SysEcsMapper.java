package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysEcsEntity;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Delete;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:06
 */
public interface SysEcsMapper extends CustomMapper<SysEcsEntity> {

    @Delete("truncate sys_ecs")
    void clearEcs();
}
