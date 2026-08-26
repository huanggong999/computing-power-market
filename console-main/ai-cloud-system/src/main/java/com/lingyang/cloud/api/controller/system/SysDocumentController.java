package com.lingyang.cloud.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingyang.cloud.entity.SysDocument;
import com.lingyang.cloud.entity.SysDocumentType;
import com.lingyang.cloud.entity.SysModuleEntity;
import com.lingyang.cloud.enums.module.ModuleTypeEnum;
import com.lingyang.cloud.mapper.SysDocumentMapper;
import com.lingyang.cloud.mapper.SysDocumentTypeMapper;
import com.lingyang.cloud.model.query.home.SysDocumentQuery;
import com.lingyang.cloud.service.SysDocumentService;
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

import java.util.ArrayList;
import java.util.List;

@Tag(name = "后台系统-文档")
@RestController
@RequestMapping("/system/document")
public class SysDocumentController {

    @Autowired
    private SysDocumentService sysDocumentService;

    @Autowired
    private SysDocumentMapper sysDocumentMapper;


    @Autowired
    private SysDocumentTypeMapper sysDocumentTypeMapper;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysDocument>> page(SysDocumentQuery query) {
        return sysDocumentService.
                getPage(PageQuery.build(query));
    }




    @Operation(summary = "查询文档详情")
    @GetMapping("/{id}")
    public Result<SysDocument> detail(@PathVariable("id") Long id) {
        SysDocument data = sysDocumentMapper.selectById(id);
        SysDocumentType type = sysDocumentTypeMapper.selectById(data.getTypeId());
        List<Long> types = new ArrayList<>();
        if (type.getLevel().equals(4)) {
            SysDocumentType type3 = sysDocumentTypeMapper.selectById(type.getParentId());
            SysDocumentType type2 = sysDocumentTypeMapper.selectById(type3.getParentId());
            SysDocumentType type1 = sysDocumentTypeMapper.selectById(type2.getParentId());
            types.add(type1.getId());
            types.add(type2.getId());
            types.add(type3.getId());
            types.add(type.getId());
        }
        if (type.getLevel().equals(3)) {
            SysDocumentType type2 = sysDocumentTypeMapper.selectById(type.getParentId());
            SysDocumentType type1 = sysDocumentTypeMapper.selectById(type2.getParentId());
            types.add(type1.getId());
            types.add(type2.getId());
            types.add(type.getId());
        }
        if (type.getLevel().equals(2)) {
            SysDocumentType type1 = sysDocumentTypeMapper.selectById(type.getParentId());
            types.add(type1.getId());
            types.add(type.getId());
        }
        data.setTypeTypes(types);
        return Result.success(data);
    }

    @Operation(summary = "添加文档")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysDocument entity) {
        Long count = sysDocumentMapper.selectCount(
                new LambdaQueryWrapper<SysDocument>()
                        .eq(SysDocument::getTypeId, entity.getTypeId())
        );
        if (count != 0) {
            return Result.error("文档分类已分配文档");
        }
        sysDocumentMapper.insert(entity);
        return  Result.success();
    }

    @Operation(summary = "修改文档")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysDocument entity) {
        Long count = sysDocumentMapper.selectCount(
                new LambdaQueryWrapper<SysDocument>()
                        .eq(SysDocument::getTypeId, entity.getTypeId())
                        .ne(SysDocument::getId, entity.getId())
        );
        if (count != 0) {
            return Result.error("文档分类已分配文档");
        }
        sysDocumentMapper.updateById(entity);
        return  Result.success();
    }


    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysDocumentMapper.deleteById(id);
        return Result.success();
    }



}
