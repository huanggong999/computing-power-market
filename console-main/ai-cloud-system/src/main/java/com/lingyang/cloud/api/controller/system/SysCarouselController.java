package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysCarouselEntity;
import com.lingyang.cloud.mapper.SysCarouselMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.model.query.carousel.SysCarouselQuery;
import com.lingyang.cloud.service.SysApplyService;
import com.lingyang.cloud.service.SysCarouselService;
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


@Tag(name = "后台系统-轮播图")
@RestController
@RequestMapping("/system/carousel")
public class SysCarouselController {

    @Autowired
    private SysCarouselService sysCarouselService;

    @Autowired
    private SysCarouselMapper sysCarouselMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysCarouselEntity>> page(SysCarouselQuery query) {
        return sysCarouselService.getPage(PageQuery.build(query));
    }

    @Operation(summary = "查询轮播图")
    @GetMapping("/{id}")
    public Result<SysCarouselEntity> detail(@PathVariable("id") Long id) {
        return Result.success(sysCarouselService.getById(id));
    }


    @Operation(summary = "添加轮播图")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysCarouselEntity entity) {
        return Result.result(sysCarouselService.save(entity));
    }

    @Operation(summary = "修改轮播图")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysCarouselEntity entity) {
        return Result.result(sysCarouselService.update(entity));
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysCarouselMapper.deleteById(id);
        return Result.success();
    }

}
