package com.lingyang.cloud.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCarouselEntity;
import com.lingyang.cloud.entity.SysNews;
import com.lingyang.cloud.mapper.SysCarouselMapper;
import com.lingyang.cloud.mapper.SysNewsMapper;
import com.lingyang.cloud.model.query.SysNewsQuery;
import com.lingyang.cloud.model.query.carousel.SysCarouselQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台系统-新闻")
@RestController
@RequestMapping("/system/news")
public class SysNewsController {

    @Autowired
    private SysNewsMapper sysNewsMapper;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysNews>> page(SysNewsQuery q) {
        PageQuery<SysNewsQuery> pageQuery = PageQuery.build(q);
        SysNewsQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysNews> queryWrapper = Wrappers.lambdaQuery(SysNews.class)
                .like(StringUtils.isNotBlank(query.getName()), SysNews::getName, query.getName())
                .orderByAsc(SysNews::getSort);
        return Result.success(Optional.of(sysNewsMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysNews> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Operation(summary = "查询新闻")
    @GetMapping("/{id}")
    public Result<SysNews> detail(@PathVariable("id") Long id) {
        return Result.success(sysNewsMapper.selectById(id));
    }


    @Operation(summary = "添加新闻")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody @Valid SysNews entity) {
        sysNewsMapper.insert(entity);
        return Result.success();
    }

    @Operation(summary = "修改新闻")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody @Valid SysNews entity) {
        sysNewsMapper.updateById(entity);
        return Result.success();
    }

    @Operation(summary = "删除")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam(value = "id") Long id) {
        sysNewsMapper.deleteById(id);
        return Result.success();
    }




}
