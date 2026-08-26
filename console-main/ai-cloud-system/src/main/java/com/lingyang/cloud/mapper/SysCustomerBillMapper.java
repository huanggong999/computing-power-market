package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.model.dto.AppletsIncomeListDTO;
import com.lingyang.cloud.model.query.customer.SysCustomerBillQuery;
import com.lingyang.cloud.model.vo.applets.AppletsIncomeListVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/28 17:24
 */
public interface SysCustomerBillMapper extends CustomMapper<SysCustomerBillEntity> {
    List<SysCustomerBillOverviewVO> getBillOverviewPage(SysCustomerBillQuery query);

    List<SysCustomerBillEntity> getList(SysCustomerBillQuery query);

    List<AppletsIncomeListDTO> incomeList(@Param("param") AppletsIncomeListVO vo);
}
