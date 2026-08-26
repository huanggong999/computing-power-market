package com.lingyang.cloud.api.controller.pc;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcInvoiceCompanyDTO;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysInvoiceTitleEntity;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysInvoiceTitleMapper;
import com.lingyang.cloud.service.SysInvoiceTitleService;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 吴思镇
 */
@Tag(name = "pc端-发票抬头")
@RestController
@RequestMapping("/pc/invoice/title")
public class PcInvoiceTitleController {

    @Autowired
    private SysInvoiceTitleService sysInvoiceTitleService;

    @Autowired
    private SysInvoiceTitleMapper sysInvoiceTitleMapper;

    @Resource
    private SysCustomerMapper sysCustomerMapper;

    @Operation(summary = "获取发票抬头列表")
    @GetMapping("/list")
    public Result<List<SysInvoiceTitleEntity>> list() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        List<SysInvoiceTitleEntity> list = sysInvoiceTitleMapper.selectList(new LambdaQueryWrapper<SysInvoiceTitleEntity>()
                .eq(SysInvoiceTitleEntity::getCustomerId, userId)
                .orderByDesc(SysInvoiceTitleEntity::getCreateTime));
        return Result.success(list);
    }

    @Operation(summary = "新增发票抬头")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysInvoiceTitleEntity entity) {
        entity.setCustomerId(SecurityContext.getUserInfo().getUserId());
        sysInvoiceTitleMapper.insert(entity);
        return Result.success();
    }

    @Operation(summary = "查询用户企业认证信息")
    @GetMapping("/companyVerify/{customerId}")
    public Result<PcInvoiceCompanyDTO> companyVerify(@PathVariable String customerId) {
        SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                .eq(SysCustomerEntity::getId, customerId)
                .eq(SysCustomerEntity::getType, 2));
        PcInvoiceCompanyDTO pcInvoiceCompanyDTO = BeanUtil.copyProperties(sysCustomerEntity, PcInvoiceCompanyDTO.class);
        return Result.success(pcInvoiceCompanyDTO);
    }

    @Operation(summary = "修改发票抬头")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysInvoiceTitleEntity entity) {
        sysInvoiceTitleMapper.updateById(entity);
        return Result.success();
    }


    @Operation(summary = "删除发票抬头")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysInvoiceTitleMapper.deleteById(id);
        return Result.success();
    }
}
