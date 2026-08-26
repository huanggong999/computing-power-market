package com.lingyang.cloud.api.controller.pc;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.entity.SysInvoiceEmailEntity;
import com.lingyang.cloud.mapper.SysInvoiceEmailMapper;
import com.lingyang.cloud.service.SysInvoiceEmailService;
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
@Tag(name = "pc端-发票抬头邮箱")
@RestController
@RequestMapping("/pc/invoice/email")
public class PcInvoiceEmailController {

    @Autowired
    private SysInvoiceEmailService sysInvoiceEmailService;

    @Autowired
    private SysInvoiceEmailMapper sysInvoiceEmailMapper;

    @Operation(summary = "获取发票抬头电子邮箱")
    @GetMapping("/list")
    public Result<List<SysInvoiceEmailEntity>> list() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        List<SysInvoiceEmailEntity> list = sysInvoiceEmailMapper.selectList(new LambdaQueryWrapper<SysInvoiceEmailEntity>()
                .eq(SysInvoiceEmailEntity::getCustomerId, userId)
                .orderByDesc(SysInvoiceEmailEntity::getCreateTime));
        return Result.success(list);
    }

    @Operation(summary = "新增发票抬头电子邮箱")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysInvoiceEmailEntity entity) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        entity.setCustomerId(userId);
        sysInvoiceEmailMapper.insert(entity);
        return Result.success();
    }

    @Operation(summary = "修改发票抬头电子邮箱")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysInvoiceEmailEntity entity) {
        sysInvoiceEmailMapper.updateById(entity);
        return Result.success();
    }

    @Operation(summary = "删除发票抬头电子邮箱")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysInvoiceEmailMapper.deleteById(id);
        return Result.success();
    }

    @Operation(summary = "设置默认电子邮箱")
    @PostMapping("/setDefault")
    public Result<Void> setDefault(@RequestBody @Valid SysInvoiceEmailEntity entity) {
        return Result.result(sysInvoiceEmailService.setDefault(entity));
    }

}
