package com.lingyang.cloud.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.module.ModuleTypeEnum;
import com.lingyang.cloud.mapper.SysRechargeActivityMapper;
import com.lingyang.cloud.mapper.SysRechargeActivityRewardsMapper;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysRechargeQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台系统-充值活动")
@RestController
@RequestMapping("/system/recharge")
public class SysRechargeActivityController {

    @Autowired
    private SysRechargeActivityMapper sysRechargeActivityMapper;

    @Autowired
    private SysRechargeActivityRewardsMapper sysRechargeActivityRewardsMapper;


    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysRechargeActivity>> page(SysRechargeQuery q) {
         PageQuery<SysRechargeQuery> pageQuery = PageQuery.build(q);
        SysRechargeQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysRechargeActivity> queryWrapper = Wrappers.lambdaQuery(SysRechargeActivity.class)
                .eq(query.getStatus() != null, SysRechargeActivity::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysRechargeActivity::getName, query.getName())
                .ge(StringUtils.isNotBlank(query.getStartTime()), SysRechargeActivity::getRechargeStartTime, query.getStartTime())
                .le(StringUtils.isNotBlank(query.getEndTime()), SysRechargeActivity::getRechargeStartTime, query.getEndTime())
                .orderByDesc(SysRechargeActivity::getCreateTime);
        List<SysRechargeActivity> value = sysRechargeActivityMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {

        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysRechargeActivity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Operation(summary = "查询")
    @GetMapping("/{id}")
    public Result<SysRechargeActivity> detail(@PathVariable("id") Long id) {
        SysRechargeActivity data = sysRechargeActivityMapper.selectById(id);
        List<SysRechargeActivityRewards> rewards = sysRechargeActivityRewardsMapper.selectList(
                new LambdaQueryWrapper<SysRechargeActivityRewards>()
                        .eq(SysRechargeActivityRewards::getActivityId, id)
                        .orderByAsc(SysRechargeActivityRewards::getLevel)
        );
        data.setReceivers(rewards);
        return Result.success(data);
    }


    @Operation(summary = "添加")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody  SysRechargeActivity entity) {
        sysRechargeActivityMapper.insert(entity);
        List<SysRechargeActivityRewards> receivers = entity.getReceivers();
        for (SysRechargeActivityRewards receiver : receivers) {
            receiver.setActivityId(entity.getId());
        }
        sysRechargeActivityRewardsMapper.batchInsert(receivers);
        return Result.success();
    }

    @Operation(summary = "修改")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody  SysRechargeActivity entity) {
        sysRechargeActivityMapper.updateById(entity);
        List<SysRechargeActivityRewards> receivers = entity.getReceivers();
        for (SysRechargeActivityRewards receiver : receivers) {
            receiver.setId(null);
            receiver.setActivityId(entity.getId());
        }

        sysRechargeActivityRewardsMapper.delete(
                new LambdaQueryWrapper<SysRechargeActivityRewards>()
                        .eq(SysRechargeActivityRewards::getActivityId, entity.getId())
        );
        sysRechargeActivityRewardsMapper.batchInsert(receivers);
        return Result.success();
    }

    @Operation(summary = "删除活动")
    @GetMapping("/delete")
    public Result<Void> delete(@RequestParam Long id) {
        sysRechargeActivityMapper.deleteById(id);
        return Result.success();
    }


}
