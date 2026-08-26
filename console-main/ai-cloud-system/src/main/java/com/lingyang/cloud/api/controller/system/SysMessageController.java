package com.lingyang.cloud.api.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysExtend;
import com.lingyang.cloud.entity.SysMessage;
import com.lingyang.cloud.entity.SysNetworkFormEntity;
import com.lingyang.cloud.entity.SysNetworkProductEntity;
import com.lingyang.cloud.mapper.SysExtendMapper;
import com.lingyang.cloud.mapper.SysMessageMapper;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysMessageQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "后台系统-消息")
@RestController
@RequestMapping("/system/message")
public class SysMessageController {



    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Operation(summary = "获取分页列表", parameters = {
            @Parameter(name = PageQuery.PAGE_NO_NAME, description = "当前页，默认为 = 1", in = ParameterIn.QUERY),
            @Parameter(name = PageQuery.PAGE_SIZE_NAME, description = "当前页数量，默认为 = 10", in = ParameterIn.QUERY)
    })
    @GetMapping("/page")
    public Result<PageResult<SysMessage>> page(SysMessageQuery s) {
         PageQuery<SysMessageQuery> pageQuery = PageQuery.build(s);
        SysMessageQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysMessage> queryWrapper = Wrappers.lambdaQuery(SysMessage.class)
                .eq(query.getStatus() != null, SysMessage::getStatus, query.getStatus())
                .orderByDesc(SysMessage::getCreateTime);
        List<SysMessage> value = sysMessageMapper.selectList(queryWrapper);
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysMessage> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }


    @Operation(summary = "已读一条")
    @GetMapping("/read")
    public Result<Void> read(@RequestParam("id") Long id) {
        sysMessageMapper.update(new SysMessage(), new LambdaUpdateWrapper<SysMessage>()
                .set(SysMessage::getStatus, 2)
                .eq(SysMessage::getId, id)
        );
        return Result.success();
    }

    @Operation(summary = "全部已读")
    @GetMapping("/readAll")
    public Result<Void> readAll() {
        sysMessageMapper.update(new SysMessage(), new LambdaUpdateWrapper<SysMessage>()
                .set(SysMessage::getStatus, 2)
        );
        return Result.success();
    }




}
