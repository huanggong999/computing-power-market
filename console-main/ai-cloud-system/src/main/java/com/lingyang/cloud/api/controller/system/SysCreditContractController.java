package com.lingyang.cloud.api.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lingyang.cloud.entity.SysCreditContract;
import com.lingyang.cloud.entity.SysCustomerBillEntity;
import com.lingyang.cloud.entity.SysCustomerCreditLineEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysCreditContractMapper;
import com.lingyang.cloud.mapper.SysCustomerBillMapper;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.model.dto.CreditContractDTO;
import com.lingyang.cloud.model.dto.SendCreditContractDTO;
import com.lingyang.cloud.model.query.home.SysCreditContractQuery;
import com.lingyang.cloud.service.SysCustomerCreditLineService;
import com.lingyang.cloud.utils.esign.*;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "后台系统-授信额合同")
@RequestMapping("/system/creditContract")
public class SysCreditContractController {

    @Autowired
    private SysCreditContractMapper sysCreditContractMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Autowired
    private SysCustomerCreditLineService sysCustomerCreditLineService;

    @Autowired
    private SysCustomerBillMapper sysCustomerBillMapper;


    @Value("${contract.notifyUrl}")
    private String notifyUrl;

    @Operation(summary = "合同分页", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCreditContract>> page(SysCreditContractQuery pageQuery) {
        PageQuery<SysCreditContractQuery> build = PageQuery.build(pageQuery);
        SysCreditContractQuery query = build.getQuery();
        build.startPage();
        LambdaQueryWrapper<SysCreditContract> queryWrapper = Wrappers.lambdaQuery(SysCreditContract.class)
                .like(StringUtils.isNotBlank(query.getContractNo()), SysCreditContract::getContractNo, query.getContractNo())
                .orderByDesc(SysCreditContract::getCreateTime);
        return Result.success(Optional.of(sysCreditContractMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysCreditContract> result = PageResult.of(entities);
                    List<SysCreditContract> list = result.getList();
                    for (SysCreditContract sysContract : list) {
                        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(sysContract.getUserId());
                        if (sysCustomerEntity != null) {
                            sysContract.setCustomerName(sysCustomerEntity.getCustomerName());
                            sysContract.setCustomerPhone(sysCustomerEntity.getPhone());
                        }

                        if (sysContract.getType().equals(1)) {
                            if (sysContract.getDownloadTime() == null || (sysContract.getDownloadTime() != null && DateUtils.addMinutes(sysContract.getDownloadTime(), 50).before(new Date()) && sysContract.getStatus() == 3)) {
                                //下载已签署文件及附属材料
                                EsignHttpResponse fileDownloadUrl = null;
                                try {
                                    fileDownloadUrl = SignDemo.fileDownloadUrl(sysContract.getSignFlowId());
                                    JSONObject jsonObject = JSON.parseObject(fileDownloadUrl.getBody());
                                    log.info("下载签署合同： {}", fileDownloadUrl.getBody());
                                    sysContract.setFileDownloadUrl(jsonObject.getJSONObject("data").getJSONArray("files").getJSONObject(0).getString("downloadUrl"));
                                    sysContract.setDownloadTime(new Date());
                                    sysCreditContractMapper.updateById(sysContract);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }


    @Operation(summary = "发起合同")
    @PostMapping("/send-contract")
    public Result<Void> sendContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysCreditContract contract = sysCreditContractMapper.selectById(d.getId());
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (contract.getStatus().equals(2)) {
            return Result.error("合同待签署");
        }
        if (contract.getStatus().equals(3)) {
            return Result.error("合同已签署");
        }
        if (contract.getStatus().equals(4)) {
            return Result.error("合同已过期");
        }
        contract.setAmount(d.getAmount());
        contract.setZq(d.getZq());
        contract.setContractNo("HT" + System.currentTimeMillis());
        contract.setStatus(2);

        // 生成模版文件
        Gson gson = new Gson();
        EsignHttpResponse createByDocTemplate = TemplateDemo.createCreditByDocTemplate(contract);
        JsonObject createByDocTemplateObject = gson.fromJson(createByDocTemplate.getBody(), JsonObject.class);
        if (!createByDocTemplateObject.get("code").toString().equals("0")) {
            return Result.error(createByDocTemplateObject.get("message").toString());
        }
        String fileId = createByDocTemplateObject.getAsJsonObject("data").get("fileId").getAsString();
        String fileDownloadUrl = createByDocTemplateObject.getAsJsonObject("data").get("fileDownloadUrl").getAsString();
        log.info("填充后文件id: {}", fileId);
        log.info("文件下载链接: {}", fileDownloadUrl);

        contract.setFileId(fileId);
        contract.setFileDownloadUrl(fileDownloadUrl);

        EsignHttpResponse file = null;

        // 个人
        if (contract.getClientContactPerson().length() <= 5) {
            file = PlatformSignToC.createCreditByFile(contract, notifyUrl);
        } else {
            file = PlatformSignToC.createCompanyCreditByFile(contract, notifyUrl);
        }

        String body = file.getBody();
        log.info("发起签署: {}", body);
        JsonObject js = gson.fromJson(body, JsonObject.class);
        if (!js.get("code").toString().equals("0")) {
            return Result.error(js.get("message").toString());
        }
        String signFlowId = js.getAsJsonObject("data").get("signFlowId").getAsString();
        contract.setSignFlowId(signFlowId);


        EsignHttpResponse response = SignDemo.signUrl(signFlowId, contract.getClientContactPhone());
        JsonObject signUrlJsonObject = gson.fromJson(response.getBody(), JsonObject.class);
        JsonObject signUrlData = signUrlJsonObject.getAsJsonObject("data");
        String shortUrl = signUrlData.get("shortUrl").getAsString();
        log.info("合同短链: {}", shortUrl);
        contract.setSignUrl(shortUrl);

        sysCreditContractMapper.updateById(contract);
        return Result.success();
    }


    @Operation(summary = "撤回合同")
    @PostMapping("/revoke-contract")
    public Result<Void> revokeContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysCreditContract contract = sysCreditContractMapper.selectById(d.getId());
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (contract.getStatus().equals(3)) {
            return Result.error("合同已签署，无法撤回");
        }
        if (contract.getStatus().equals(4)) {
            return Result.error("合同已过期,无法撤回");
        }
        if (contract.getStatus().equals(7)) {
            return Result.error("合同已撤回,请勿重复操作");
        }

        // 生成模版文件
        Gson gson = new Gson();
        EsignHttpResponse s = PlatformSignToC.revoke(contract.getSignFlowId());
        JsonObject createByDocTemplateObject = gson.fromJson(s.getBody(), JsonObject.class);
        if (!createByDocTemplateObject.get("code").toString().equals("0")) {
            return Result.error(createByDocTemplateObject.get("message").toString());
        }
        contract.setStatus(7);
        sysCreditContractMapper.updateById(contract);
        return Result.success();
    }


    @Operation(summary = "审核纸质合同")
    @PostMapping("/verify-contract")
    public Result<Void> verifyContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysCreditContract contract = sysCreditContractMapper.selectById(d.getId());
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
            sysCreditContractMapper.updateById(contract);
        } else {

            contract.setCompleteTime(new Date());
            contract.setStatus(3);
            sysCreditContractMapper.updateById(contract);


            // 发放授信额
            SysCustomerCreditLineEntity entity = new SysCustomerCreditLineEntity();
            entity.setCustomerId(contract.getUserId());
            entity.setTotalAmount(contract.getAmount());
//                        entity.setUseAmount();
            entity.setUseAmount(BigDecimal.ZERO);
            entity.setStatus(StatusEnum.OK);
            entity.setUseTimeStart(entity.getUseTimeStart() == null ? com.lingyang.common.core.utils.DateUtils.getNowDate() : entity.getUseTimeStart());
            entity.setUseTimeEnd(com.lingyang.common.core.utils.DateUtils.addYears(entity.getUseTimeStart(), 1));
            sysCustomerCreditLineService.addCustomerCreditLine(entity);
            // 增加账单记录
            SysCustomerBillEntity billEntity = new SysCustomerBillEntity();
            billEntity.setPayPrice(contract.getAmount());
            billEntity.setCustomerId(contract.getUserId());
            billEntity.setBill(com.lingyang.common.core.utils.DateUtils.parseDateToStr("yyyy-MM", new Date()));
            billEntity.setBillNo(IdUtils.simpleUUID());
            billEntity.setBillDate(new Date());
            billEntity.setSourceType(SourceTypeEnum.CREDIT_AMOUNT_RECHARGE);
            billEntity.setBillType("充值-使用");
            billEntity.setSettleType("充值");
            billEntity.setArrearsAmount(BigDecimal.ZERO);
            billEntity.setCreditLineAmount(contract.getAmount());
            billEntity.setPayStatus(OrderStatusEnum.PAID);
            billEntity.setPayTime(new Date());
            billEntity.setBillStartTime(new Date());
            billEntity.setBillEndTime(new Date());
            sysCustomerBillMapper.insert(billEntity);

        }
        return Result.success();
    }


    @Operation(summary = "上传签署扫描件")
    @PostMapping("/upload-contract")
    public Result<Void> uploadContract(@RequestBody SendCreditContractDTO d) throws EsignDemoException {
        SysCreditContract contract = sysCreditContractMapper.selectById(d.getId());
        if (contract == null) {
            return Result.error("合同不存在");
        }
        if (contract.getType().equals(1)) {
            return Result.error("不是线下合同");
        }
        if (contract.getStatus().equals(3)) {
            return Result.error("合同已签署");
        }
        contract.setAmount(d.getAmount());
        contract.setZq(d.getZq());
        contract.setContractNo("HT" + System.currentTimeMillis());
        contract.setStatus(8);
        contract.setUploadImg(d.getUploadImg());
        sysCreditContractMapper.updateById(contract);
        return Result.success();
    }



}
