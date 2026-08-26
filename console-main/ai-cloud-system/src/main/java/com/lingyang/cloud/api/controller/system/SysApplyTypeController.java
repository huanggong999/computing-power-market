package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.entity.SysCustomerVoucherEntity;
import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.mapper.SysApplyTypeMapper;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.model.vo.apply.SysApplyTypeListVO;
import com.lingyang.cloud.service.SysApplyTypeService;
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

import java.util.List;

@Tag(name = "后台系统-应用分类")
@RestController
@RequestMapping("/system/apply-type")
public class SysApplyTypeController {

    @Autowired
    private SysApplyTypeService sysApplyTypeService;

    @Autowired
    private SysApplyTypeMapper sysApplyTypeMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysApplyTypeEntity>> page() {
        return sysApplyTypeService.getPage(PageQuery.build());
    }


    @Operation(summary = "获取一级分类列表")
    @PostMapping("/first-list")
    public Result<List<SysApplyTypeEntity>> firstList() {
        return Result.success(sysApplyTypeService.firstList());
    }

    @Operation(summary = "获取所有分类列表")
    @PostMapping("/all")
    public Result<List<SysApplyTypeListVO>> listAll() {
        return Result.success(sysApplyTypeService.getAll());
    }

    @Operation(summary = "添加应用分类")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysApplyTypeEntity entity) {
        return Result.result(sysApplyTypeService.save(entity));
    }

    @Operation(summary = "修改应用分类")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysApplyTypeEntity entity) {
        return Result.result(sysApplyTypeService.update(entity));
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysApplyTypeMapper.deleteById(id);
        return Result.success();
    }


}
