package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.model.dto.PcInvoiceBillInfoDTO;
import com.lingyang.cloud.model.query.invoice.SysInvoiceQuery;
import com.lingyang.cloud.model.vo.invoice.SysInvoiceManageVO;
import com.lingyang.cloud.service.SysInvoiceManageService;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 14:33
 */
@Tag(name = "后台系统-发票管理")
@RestController
@RequestMapping("/system/invoice/manage")
public class SysInvoiceManageController {

    @Autowired
    private SysInvoiceManageService sysInvoiceManageService;

    @Operation(summary = "开票列表")
    @PostMapping("/list")
    public Result<List<PcInvoiceBillInfoDTO>> getInvoiceList(@RequestBody SysInvoiceQuery query) {
        return sysInvoiceManageService.list(query);
    }

    @Operation(summary = "开票操作")
    @PostMapping("/billing/operation")
    public Result<Void> billingOperation(@RequestBody SysInvoiceManageVO vo) {
        return sysInvoiceManageService.billingOperation(vo);
    }
}
