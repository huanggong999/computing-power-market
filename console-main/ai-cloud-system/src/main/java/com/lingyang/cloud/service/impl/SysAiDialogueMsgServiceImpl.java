package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysAiDialogue;
import com.lingyang.cloud.entity.SysAiDialogueMsg;
import com.lingyang.cloud.mapper.SysAiDialogueMsgMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.model.query.home.SysAIMsgQuery;
import com.lingyang.cloud.service.SysAiDialogueMsgService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAiDialogueMsgServiceImpl implements SysAiDialogueMsgService {
    @Autowired
    private SysAiDialogueMsgMapper sysAiDialogueMsgMapper;
    @Override
    public Result<PageResult<SysAiDialogueMsg>> getPage(PageQuery<SysAIMsgQuery> pageQuery) {
        SysAIMsgQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysAiDialogueMsg> queryWrapper = Wrappers.lambdaQuery(SysAiDialogueMsg.class)
                .eq(SysAiDialogueMsg::getDialogueId, query.getDialogueId())
                .orderByDesc(SysAiDialogueMsg::getCreateTime);
        return Result.success(Optional.of(sysAiDialogueMsgMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysAiDialogueMsg> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
