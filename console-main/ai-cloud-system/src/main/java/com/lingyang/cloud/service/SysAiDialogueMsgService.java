package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysAiDialogueMsg;
import com.lingyang.cloud.model.query.home.SysAIMsgQuery;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

public interface SysAiDialogueMsgService {
    Result<PageResult<SysAiDialogueMsg>> getPage(PageQuery<SysAIMsgQuery> build);
}
