package com.lingyang.cloud.api.controller.pc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCreditContract;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCreditContractMapper;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.model.dto.CreditContractDTO;
import com.lingyang.cloud.model.query.home.SysCreditContractQuery;
import com.lingyang.cloud.utils.esign.EsignDemoException;
import com.lingyang.cloud.utils.esign.EsignHttpResponse;
import com.lingyang.cloud.utils.esign.SignDemo;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@Tag(name = "pc端-授信额合同")
@RequestMapping("/pc/credit-contract")
public class PcCreditContractController {

    @Autowired
    private SysCreditContractMapper sysCreditContractMapper;


    @Autowired
    private SysCustomerMapper sysCustomerMapper;

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
                .eq(SysCreditContract::getUserId, SecurityContext.getUserInfo().getUserId())
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


    @Operation(summary = "申请合同")
    @PostMapping("/apply-contract")
    public Result<Void> orderContract(@RequestBody CreditContractDTO d) throws EsignDemoException {
        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectById(SecurityContext.getUserInfo().getUserId());
        if (d.getId() == null) {
            SysCreditContract sysCreditContract = new SysCreditContract();
            sysCreditContract.setUserId(SecurityContext.getUserInfo().getUserId());
            // sysCreditContract.setContractNo("HT" + System.currentTimeMillis());
            sysCreditContract.setClientContactPerson(d.getClientContactPerson());
            sysCreditContract.setClientContactPhone(d.getClientContactPhone());
            sysCreditContract.setClientContactPersonName(d.getClientContactPersonName());
            sysCreditContract.setStatus(1);
            sysCreditContract.setType(d.getType());
            sysCreditContract.setUserType(sysCustomerEntity.getType());
            sysCreditContract.setRemark(d.getRemark());
            sysCreditContractMapper.insert(sysCreditContract);
        } else {
            SysCreditContract sysCreditContract = new SysCreditContract();
            sysCreditContract.setId(d.getId());
            sysCreditContract.setUserId(SecurityContext.getUserInfo().getUserId());
            // sysCreditContract.setContractNo("HT" + System.currentTimeMillis());
            sysCreditContract.setClientContactPerson(d.getClientContactPerson());
            sysCreditContract.setClientContactPhone(d.getClientContactPhone());
            sysCreditContract.setClientContactPersonName(d.getClientContactPersonName());
            sysCreditContract.setStatus(1);
            sysCreditContract.setType(d.getType());
            sysCreditContract.setUserType(sysCustomerEntity.getType());
            sysCreditContract.setRemark(d.getRemark());
            sysCreditContractMapper.updateById(sysCreditContract);
        }
        return Result.success();
    }

    @Operation(summary = "上传纸质合同")
    @PostMapping("/upload-contract")
    public Result<Void> uploadContract(@RequestBody CreditContractDTO d) throws EsignDemoException {
        if (d.getId() != null) {
            SysCreditContract sysCreditContract = new SysCreditContract();
            sysCreditContract.setId(d.getId());
            sysCreditContract.setStatus(2);
            sysCreditContract.setSignUploadImg(d.getSignUploadImg());
            sysCreditContractMapper.updateById(sysCreditContract);
        }
        return Result.success();
    }


}


