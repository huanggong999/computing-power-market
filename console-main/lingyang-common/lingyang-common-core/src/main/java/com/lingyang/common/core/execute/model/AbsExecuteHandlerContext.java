package com.lingyang.common.core.execute.model;

import com.lingyang.common.core.utils.IdUtils;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/10/9 11:43
 */
@Data
public abstract class AbsExecuteHandlerContext {

    private String uid;

    public AbsExecuteHandlerContext() {
        uid = IdUtils.randomUUID();
    }
}