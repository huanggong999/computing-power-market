package com.lingyang.common.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务操作类型
 * 
 * @author scrm
 */
@AllArgsConstructor
@Getter
public enum BusinessType
{

    /**
     * 其它
     */
    OTHER(0),
    /**
     * 获取
     */
    GET(1),
    /**
     * 新增
     */
    INSERT(2),

    /**
     * 修改
     */
    UPDATE(3),

    /**
     * 删除
     */
    DELETE(4),

    /**
     * 授权
     */
    GRANT(5),

    /**
     * 导出
     */
    EXPORT(6),

    /**
     * 导入
     */
    IMPORT(7),

    /**
     * 强退
     */
    FORCE(8),

    /**
     * 清空数据
     */
    CLEAN(9),

    ;
    private final int code;
}