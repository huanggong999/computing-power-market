package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysActiveCenterEntity;
import com.lingyang.cloud.model.query.active.SysActiveQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 吴思镇
 */
public interface SysActiveCenterMapper extends CustomMapper<SysActiveCenterEntity> {
    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    List<SysActiveCenterEntity> getPage(@Param("param") SysActiveQuery param);
}
