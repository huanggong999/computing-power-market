package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.mapper.SysNetworkFormMapper;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.service.SysHomeEcsService;
import com.lingyang.cloud.service.SysNetworkFormService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/network-form")
@Tag(name = "后台系统-咨询表单")
public class SysNetworkFormController {


    @Autowired
    private SysNetworkFormService sysNetworkFormService;

    @Autowired
    private SysNetworkFormMapper sysNetworkFormMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNetworkFormEntity>> page(SysHomeEcsQuery query) {
        return sysNetworkFormService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询咨询表单")
    @GetMapping("/{id}")
    public Result<SysNetworkFormEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysNetworkFormService.getById(id));
    }


    @Operation(summary = "添加咨询表单")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysNetworkFormEntity entity) {
        sysNetworkFormService.save(entity);
        return Result.result(true);
    }

    @Operation(summary = "修改咨询表单")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysNetworkFormEntity entity) {
        sysNetworkFormService.update(entity);
        return Result.result(true);
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysNetworkFormMapper.deleteById(id);
        return Result.success();
    }


}
