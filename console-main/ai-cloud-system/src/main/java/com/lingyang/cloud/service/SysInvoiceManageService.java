package com.lingyang.cloud.service;

import com.lingyang.cloud.api.model.vo.PcInvoiceManageVO;
import com.lingyang.cloud.model.dto.PcInvoiceAmountDetailsDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillInfoDTO;
import com.lingyang.cloud.model.query.invoice.SysInvoiceQuery;
import com.lingyang.cloud.model.vo.invoice.SysInvoiceManageVO;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/13 10:22
 */
public interface SysInvoiceManageService {

    /**
     * 获取发票数据
     * @param query
     * @return
     */
    Result<List<PcInvoiceBillDTO>> getInvoiceData(SysInvoiceQuery query);

    /**
     * 获取已发票列表
     * @param query
     * @return
     */
    Result<List<PcInvoiceBillInfoDTO>> getApplyInfo(SysInvoiceQuery query);

    /**
     * 取消发票
     * @param id
     */
    void cancelInvoice(Integer id);

    /**
     * 申请发票
     * @param vo
     */
    void apply(PcInvoiceManageVO vo);

    /**
     * 获取可开发票金额
     * @return
     */
    Result<PcInvoiceAmountDetailsDTO> getTotalAmount(Long userId);

    /**
     * 导出发票详情
     * @param bills
     */
    void exportDetails(List<Long> bills);

    /**
     * 后台开票列表
     * @param query
     * @return
     */
    Result<List<PcInvoiceBillInfoDTO>> list(SysInvoiceQuery query);

    /**
     * 开票操作
     * @param vo
     * @return
     */
    Result<Void> billingOperation(SysInvoiceManageVO vo);

    Result<Boolean> hasTitleEmail(Long userId);
}
