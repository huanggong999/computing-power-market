package com.lingyang.common.web.controller;

import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.HttpResult;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.web.service.BaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: 基础api接口，默认实现响应404
 * @S: 继承BaseService
 * @QUERY： 接收分页请求参数对象
 * @EDIT： 接收新增或更新请求参数对象
 * @LIST_VO: 响应分页参数对象
 * @DETAIL_VO: 响应详情参数对象
 * @Author: 王小龙
 * @Date: 2023/8/9 14:08
 */
public abstract class BaseController<S extends BaseService<QUERY, EDIT, LIST_VO, DETAIL_VO>, QUERY, EDIT, LIST_VO , DETAIL_VO> {

    @Autowired
    protected S service;

    @Operation(summary = "获取分页数据", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<LIST_VO>> page(QUERY query) {
        return HttpResult.success(service.getPage(PageQuery.build(query)));
    }

    @Operation(summary = "获取详情", parameters = {@Parameter(name = "id",description = "数据id", in = ParameterIn.QUERY)})
    @GetMapping("/")
    public Result<DETAIL_VO> detail(@RequestParam(value = "id")  Long id) {
        return Result.success(service.getDetail(id));
    }

    @Operation(summary = "保存信息")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid EDIT t) {
        service.checkSaveParams(t);
        return Result.result(service.save(t));
    }

    @Operation(summary = "更新")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody EDIT t) {
        service.checkUpdateParams(t);
        return Result.result(service.update(t));
    }

    @Operation(summary = "删除", parameters = {@Parameter(name = "id",description = "删除的数据id", in = ParameterIn.QUERY)})
    @DeleteMapping("/")
    public Result<Void> remove(@RequestParam(value = "id") Long id) {
        service.checkRemove(id);
        return Result.result(service.remove(id));
    }

}