package com.lingyang.common.http.model;

import java.io.Serializable;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/8/20 15:05
 */
public interface HttpClientResult extends Serializable {

    /**
     * 当前请求是否成功
     * @return true/false
     */
    public abstract boolean success();

    /**
     * 获取响应消息
     * @return message
     */
    public abstract String getMsg();
}
