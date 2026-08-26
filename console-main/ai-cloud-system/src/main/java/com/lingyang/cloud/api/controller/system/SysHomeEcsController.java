package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysHomeEcsEntity;
import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.enums.module.ModuleTypeEnum;
import com.lingyang.cloud.mapper.SysHomeEcsMapper;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.model.query.module.SysModuleQuery;
import com.lingyang.cloud.service.SysHomeEcsService;
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

@Tag(name = "后台系统-算力服务器")
@RestController
@RequestMapping("/system/home-ecs")
public class SysHomeEcsController {

    @Autowired
    private SysHomeEcsService sysHomeEcsService;

    @Autowired
    private SysHomeEcsMapper sysHomeEcsMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysHomeEcsEntity>> page(SysHomeEcsQuery query) {
        return sysHomeEcsService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询算力服务器")
    @GetMapping("/{id}")
    public Result<SysHomeEcsEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysHomeEcsService.getById(id));
    }


    @Operation(summary = "添加算力服务器")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysHomeEcsEntity entity) {
        sysHomeEcsService.save(entity);
        return Result.result(true);
    }

    @Operation(summary = "修改算力服务器")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysHomeEcsEntity entity) {
        sysHomeEcsService.update(entity);
        return Result.result(true);
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysHomeEcsMapper.deleteById(id);
        return Result.success();
    }




}
