package com.lingyang.common.log.handler;

import com.lingyang.common.log.model.LogModel;

/**
 * @Description: 日志持久化处理器，不存在只打印日志
 * @Author: 王小龙
 * @Date: 2023/8/17 11:55
 */
public interface LogSavaHandler {

    /**
     * 保存日志
     * @param logModel 日志参数
     */
    void saveLog(LogModel logModel);
}
