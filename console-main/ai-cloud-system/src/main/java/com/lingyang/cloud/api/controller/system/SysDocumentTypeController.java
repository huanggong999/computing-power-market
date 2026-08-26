package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.cloud.mapper.SysDocumentTypeMapper;
import com.lingyang.cloud.service.SysDocumentTypeService;
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

@Tag(name = "后台系统-文档分类")
@RestController
@RequestMapping("/system/document-type")
public class SysDocumentTypeController {

    @Autowired
    private SysDocumentTypeService sysDocumentTypeService;

    @Autowired
    private SysDocumentTypeMapper sysDocumentTypeMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysDocumentType>> page() {
        return sysDocumentTypeService.getPage(PageQuery.build());
    }

    @Operation(summary = "新增文档分类")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysDocumentType entity) {
        sysDocumentTypeMapper.insert(entity);
        return  Result.success();
    }

    @Operation(summary = "修改文档分类")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysDocumentType entity) {
        sysDocumentTypeMapper.updateById(entity);
        return  Result.success();
    }

    @Operation(summary = "删除文档分类")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        sysDocumentTypeMapper.deleteById(id);
        return  Result.success();
    }

    @Operation(summary = "文档分类列表")
    @GetMapping("/type-list")
    public Result<List<SysDocumentType>> typeList() {
        return  sysDocumentTypeService.getTypeList();
    }




}
