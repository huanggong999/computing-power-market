package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.model.query.home.SysExtendOrderQuery;
import com.lingyang.cloud.model.query.order.SysOrderQuery;
import com.lingyang.cloud.model.vo.order.ExtendOrderVO;
import com.lingyang.common.datasource.model.CustomMapper;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:30
 */
public interface SysOrderMapper extends CustomMapper<SysOrderEntity> {

    List<SysOrderEntity> getList(SysOrderQuery orderQuery);

    List<ExtendOrderVO> getExtendList(SysExtendOrderQuery orderQuery);

    List<SysOrderEntity>  getAgicList(SysOrderQuery orderQuery);

    List<SysOrderEntity> getTestList(Long customerId);
}
