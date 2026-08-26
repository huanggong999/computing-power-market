package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysInvoiceManageEntity;
import com.lingyang.cloud.model.dto.PcInvoiceBillInfoDTO;
import com.lingyang.cloud.model.dto.excelDto.PcInvoiceExportDto;
import com.lingyang.cloud.model.query.invoice.SysInvoiceQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerBillOverviewVO;
import com.lingyang.common.datasource.model.CustomMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/10 16:58
 */
public interface SysInvoiceManageMapper extends CustomMapper<SysInvoiceManageEntity> {
    /**
     * 账期查询发票
     * @param query
     * @return
     */
    List<SysCustomerBillOverviewVO> getDateList(@Param("param") SysInvoiceQuery query);

    /**
     * 时间查询发票
     * @param query
     * @return
     */
    List<SysCustomerBillOverviewVO> getTimeList(@Param("param") SysInvoiceQuery query);

    /**
     * 获取总金额
     * @param query
     * @return
     */
    List<SysCustomerBillOverviewVO> getTotalAmount(@Param("param") SysInvoiceQuery query);

    /**
     * 获取申请发票列表
     * @param query
     * @return
     */
    List<PcInvoiceBillInfoDTO> getApplyInfo(@Param("param") SysInvoiceQuery query);

    /**
     * 获取发票详情
     * @param bills
     * @return
     */
    List<PcInvoiceExportDto> getDetails(@Param("bills") List<Long> bills);

    /**
     * 获取预开票发票
     * @param query
     * @return
     */
    List<SysCustomerBillOverviewVO> getAdvanceInvoice(@Param("param")SysInvoiceQuery query);

    List<SysCustomerBillOverviewVO> getInvoiceList(@Param("param") SysInvoiceQuery query);
}
