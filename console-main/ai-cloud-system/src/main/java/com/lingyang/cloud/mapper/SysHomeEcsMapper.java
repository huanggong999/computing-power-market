package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author EDY
 */
public interface SysHomeEcsMapper extends CustomMapper<SysHomeEcsEntity> {
    List<SysHomeEcsEntity> selectHomeEcsWithDetails(@Param("query") SysHomeEcsQuery query);
}
