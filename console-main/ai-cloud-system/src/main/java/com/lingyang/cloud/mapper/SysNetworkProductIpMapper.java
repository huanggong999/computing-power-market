package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysNetworkProductIpEntity;
import com.lingyang.cloud.model.dto.excelDto.SysProductIpExportDto;
import com.lingyang.cloud.model.query.home.SysProductIpQuery;
import com.lingyang.cloud.model.vo.product.SysProductIpExportVO;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/4 14:33
 */

public interface SysNetworkProductIpMapper extends CustomMapper<SysNetworkProductIpEntity> {
    List<SysProductIpExportDto> getIpList(@Param("param") SysProductIpExportVO vo);

    List<SysNetworkProductIpEntity> getPage(@Param("param") SysProductIpQuery query);
}
