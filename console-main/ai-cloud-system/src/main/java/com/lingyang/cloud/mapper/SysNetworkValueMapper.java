package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysNetworkValueMapper extends CustomMapper<SysNetworkValueEntity> {
    List<SysNetworkValueEntity> getPage(@Param("param")SysNetworkValueQuery query);
}
