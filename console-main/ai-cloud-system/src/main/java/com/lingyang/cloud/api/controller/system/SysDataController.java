package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.enums.module.ModuleTypeEnum;
import com.lingyang.cloud.mapper.SysModuleMapper;
import com.lingyang.cloud.model.query.module.SysModuleQuery;
import com.lingyang.cloud.service.SysModuleService;
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

@Tag(name = "后台系统-数据")
@RestController
@RequestMapping("/system/data")
public class SysDataController {


    @Autowired
    private SysModuleService sysModuleService;

    @Autowired
    private SysModuleMapper sysModuleMapper;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysModuleEntity>> page(SysModuleQuery query) {
        query.setType(ModuleTypeEnum.DATA.getCode());
        return sysModuleService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询数据")
    @GetMapping("/{id}")
    public Result<SysModuleEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysModuleService.getById(id));
    }


    @Operation(summary = "添加数据")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysModuleEntity entity) {
        entity.setType(ModuleTypeEnum.DATA.getCode());
        return Result.result(sysModuleService.save(entity));
    }

    @Operation(summary = "修改数据")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysModuleEntity entity) {
        entity.setType(ModuleTypeEnum.DATA.getCode());
        return Result.result(sysModuleService.update(entity));
    }


    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysModuleMapper.deleteById(id);
        return Result.success();
    }

}
