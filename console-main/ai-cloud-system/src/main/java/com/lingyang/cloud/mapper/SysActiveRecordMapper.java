package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysActiveRecordEntity;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 吴思镇
 */
public interface SysActiveRecordMapper extends CustomMapper<SysActiveRecordEntity> {

    /**
     * 查询活动记录
     * @param query
     * @return
     */
    List<SysActiveRecordEntity> getActiveRecord(@Param("param") SysActiveRecordQuery query);

    /**
     * 查询PC活动记录
     * @param query
     * @return
     */
    List<SysActiveRecordEntity> getPcActiveRecord(@Param("param") SysActiveRecordQuery query);
}
