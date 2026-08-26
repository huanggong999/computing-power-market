package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysAiDialogue;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import jakarta.servlet.http.HttpServletRequest;


public interface SysAiDialogueService {
    Result<PageResult<SysAiDialogue>> getPage(PageQuery<Object> build, HttpServletRequest request);
}
