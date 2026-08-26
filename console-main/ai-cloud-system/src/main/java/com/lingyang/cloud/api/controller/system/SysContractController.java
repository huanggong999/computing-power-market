package com.lingyang.cloud.api.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lingyang.cloud.entity.SysContract;
import com.lingyang.cloud.entity.SysCreditContract;
import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysContractMapper;
import com.lingyang.cloud.model.dto.OrderContractDTO;
import com.lingyang.cloud.model.dto.SendCreditContractDTO;
import com.lingyang.cloud.model.query.home.SysContractQuery;
import com.lingyang.cloud.model.vo.OrderContractCompanyInfoVO;
import com.lingyang.cloud.service.SysContractService;
import com.lingyang.cloud.utils.esign.EsignDemoException;
import com.lingyang.cloud.utils.esign.EsignHttpResponse;
import com.lingyang.cloud.utils.esign.PlatformSignToC;
import com.lingyang.cloud.utils.esign.SignDemo;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.IdUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Slf4j
@Tag(name = "后台系统-合同管理")
@RestController
@RequestMapping("/system/contract")
public class SysContractController {

    @Autowired
    private SysContractService sysContractService;

    @Autowired
    private SysContractMapper sysContractMapper;



    @Operation(summary = "订单合同分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysContract>> page(SysContractQuery query) {
        return sysContractService.getAdminPage(PageQuery.build(query));
    }

    @Operation(summary = "审核纸质合同")
    @PostMapping("/verify-contract")
    public Result<Void> verifyContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysContract contract = sysContractMapper.selectById(d.getId());
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (contract.getType().equals(1)) {
            return Result.error("不是线下合同");
        }
        if (contract.getStatus().equals(3)) {
            return Result.error("合同已签署");
        }

        // 纸质合同(1 待乙方确认，2 待归档， 3 已签署， 5 审核不通过，6 归档审核不通过 8 )
        if (d.getStatus().equals(5) || d.getStatus().equals(6)) {
            contract.setStatus(d.getStatus());
            contract.setVerifyRemark(d.getVerifyRemark());
            sysContractMapper.updateById(contract);
        } else {
            contract.setCompleteTime(new Date());
            contract.setStatus(3);
            sysContractMapper.updateById(contract);
        }
        return Result.success();
    }


    @Operation(summary = "上传签署扫描件")
    @PostMapping("/upload-contract")
    public Result<Void> uploadContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysContract contract = sysContractMapper.selectById(d.getId());
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (contract.getType().equals(1)) {
            return Result.error("不是线下合同");
        }
        if (contract.getStatus().equals(3)) {
            return Result.error("合同已签署");
        }
        contract.setStatus(8);
        contract.setUploadImg(d.getUploadImg());
        sysContractMapper.updateById(contract);
        return Result.success();
    }



}
