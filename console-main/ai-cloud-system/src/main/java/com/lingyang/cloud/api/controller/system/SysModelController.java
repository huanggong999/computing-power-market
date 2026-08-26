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

@Tag(name = "后台系统-模型")
@RestController
@RequestMapping("/system/model")
public class SysModelController {


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
        query.setType(ModuleTypeEnum.MODEL.getCode());
        return sysModuleService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询模型")
    @GetMapping("/{id}")
    public Result<SysModuleEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysModuleService.getById(id));
    }


    @Operation(summary = "添加模型")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysModuleEntity entity) {
        entity.setType(ModuleTypeEnum.MODEL.getCode());
        return Result.result(sysModuleService.save(entity));
    }

    @Operation(summary = "修改模型")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysModuleEntity entity) {
        entity.setType(ModuleTypeEnum.MODEL.getCode());
        return Result.result(sysModuleService.update(entity));
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysModuleMapper.deleteById(id);
        return Result.success();
    }


}
