package com.lingyang.cloud.api.controller.system;


import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.entity.SysNetworkProductEntity;
import com.lingyang.cloud.mapper.SysNetworkProductMapper;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.service.SysNetworkFormService;
import com.lingyang.cloud.service.SysNetworkProductService;
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
@RequestMapping("/system/network-product")
@Tag(name = "后台系统-产品列表")
public class SysNetworkProductController {

    @Autowired
    private SysNetworkProductService sysNetworkProductService;

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNetworkProductEntity>> page(SysHomeEcsQuery query) {
        return sysNetworkProductService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询产品列表")
    @GetMapping("/{id}")
    public Result<SysNetworkProductEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysNetworkProductService.getById(id));
    }


    @Operation(summary = "添加产品列表")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysNetworkProductEntity entity) {
        sysNetworkProductService.save(entity);
        return Result.result(true);
    }

    @Operation(summary = "修改产品列表")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysNetworkProductEntity entity) {
        sysNetworkProductService.update(entity);
        return Result.result(true);
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysNetworkProductMapper.deleteById(id);
        return Result.success();
    }


}
