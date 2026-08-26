package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysAiDialogue;
import com.lingyang.cloud.entity.SysAiDialogueMsg;
import com.lingyang.cloud.entity.SysApplyEntity;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import com.lingyang.cloud.mapper.SysAiDialogueMapper;
import com.lingyang.cloud.mapper.SysAiDialogueMsgMapper;
import com.lingyang.cloud.model.query.apply.SysApplyQuery;
import com.lingyang.cloud.service.SysAiDialogueService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IpUtils;
import com.lingyang.common.core.utils.Optional;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysAiDialogueServiceImpl implements SysAiDialogueService {

    @Autowired
    private SysAiDialogueMapper sysAiDialogueMapper;

    @Override
    public Result<PageResult<SysAiDialogue>> getPage(PageQuery<Object> pageQuery, HttpServletRequest request) {
        try {

                Long count = sysAiDialogueMapper.selectCount(
                        new LambdaQueryWrapper<SysAiDialogue>()
                                .eq(SysAiDialogue::getUserId, SecurityContext.getUserInfo().getUserId())
                );
                if (count == 0) {
                    SysAiDialogue dialogue = new SysAiDialogue();


                    if (SecurityContext.getUserInfo() != null) {
                        dialogue.setUserId(SecurityContext.getUserInfo().getUserId());
                    } else {
                        String header = request.getHeader("User-Agent");
                        String ipAddr = IpUtils.getIpAddr();
                        dialogue.setUserLingshi(header + "@" + ipAddr);
                    }

                    dialogue.setName("新对话");
                    sysAiDialogueMapper.insert(dialogue);
                }

        } catch (Exception e) {
            String header = request.getHeader("User-Agent");
            String ipAddr = IpUtils.getIpAddr();
            Long count = sysAiDialogueMapper.selectCount(
                    new LambdaQueryWrapper<SysAiDialogue>()
                            .eq(SysAiDialogue::getUserLingshi, header + "@" + ipAddr)
            );
            if (count == 0) {

                SysAiDialogue dialogue = new SysAiDialogue();

                dialogue.setUserLingshi(header + "@" + ipAddr);


                dialogue.setName("新对话");
                sysAiDialogueMapper.insert(dialogue);
            }
        }




        pageQuery.startPage();
        LambdaQueryWrapper<SysAiDialogue> queryWrapper = null;
        try {
                queryWrapper = Wrappers.lambdaQuery(SysAiDialogue.class)
                        .eq(SysAiDialogue::getUserId, SecurityContext.getUserInfo().getUserId())
                        .orderByDesc(SysAiDialogue::getCreateTime);
        } catch (Exception e) {
            String header = request.getHeader("User-Agent");
            String ipAddr = IpUtils.getIpAddr();
            queryWrapper = Wrappers.lambdaQuery(SysAiDialogue.class)
                    .eq(SysAiDialogue::getUserLingshi, header + "@" + ipAddr)
                    .orderByDesc(SysAiDialogue::getCreateTime);
        }

        return Result.success(Optional.of(sysAiDialogueMapper.selectList(queryWrapper))
                .flatMap(entities -> {
                    PageResult<SysAiDialogue> result = PageResult.of(entities);

                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
