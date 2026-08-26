package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerEcsWorkEntity;
import com.lingyang.cloud.model.dto.SysEcsWorkDetailDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.query.customer.SysCustomerEcsWorkQuery;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface SysCustomerEcsWorkMapper extends CustomMapper<SysCustomerEcsWorkEntity> {


    List<SysEcsWorkPageDTO> getList(@Param("param") SysCustomerEcsWorkQuery query);

    SysEcsWorkDetailDTO getDetail(Long id);
}
