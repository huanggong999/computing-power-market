package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.api.model.vo.PcInvoiceManageVO;
import com.lingyang.cloud.model.dto.PcInvoiceAmountDetailsDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillDTO;
import com.lingyang.cloud.model.dto.PcInvoiceBillInfoDTO;
import com.lingyang.cloud.model.query.invoice.SysInvoiceQuery;
import com.lingyang.cloud.service.SysInvoiceManageService;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 吴思镇
 */
@Tag(name = "pc端-发票管理")
@RestController
@RequestMapping("/pc/invoice/manage")
public class PcInvoiceManageController {

    @Autowired
    private SysInvoiceManageService sysInvoiceManageService;

    @Operation(summary = "可开发票金额统计")
    @GetMapping("/amount/list")
    public Result<PcInvoiceAmountDetailsDTO> amountList() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        return sysInvoiceManageService.getTotalAmount(userId);
    }

    @Operation(summary = "可申请开票数据")
    @PostMapping("/list")
    public Result<List<PcInvoiceBillDTO>> list(@RequestBody SysInvoiceQuery query) {
        return sysInvoiceManageService.getInvoiceData(query);
    }

    @Operation(summary = "申请开票")
    @PostMapping("/apply")
    public Result<Void> apply(@RequestBody @Valid PcInvoiceManageVO vo) {
        sysInvoiceManageService.apply(vo);
        return Result.success();
    }

    @Operation(summary = "判断用户是否有抬头和邮箱")
    @GetMapping("/has/title/email")
    public Result<Boolean> hasTitleEmail() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        return sysInvoiceManageService.hasTitleEmail(userId);
    }

    @Operation(summary = "已申请开票列表")
    @PostMapping("/apply/info")
    public Result<List<PcInvoiceBillInfoDTO>> applyInfo(@RequestBody SysInvoiceQuery query) {
        return sysInvoiceManageService.getApplyInfo(query);
    }

    @Operation(summary = "取消开票")
    @GetMapping("/cancel/invoice/{id}")
    public Result<Void> cancelInvoice( @PathVariable Integer id) {
        sysInvoiceManageService.cancelInvoice(id);
        return Result.success();
    }

    @Operation(summary = "导出明细")
    @PostMapping("/export/details")
    public void exportDetails( @RequestBody List<Long> bills) {
        sysInvoiceManageService.exportDetails(bills);
    }
}
