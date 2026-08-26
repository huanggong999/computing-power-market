package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 17:52
 */
@AllArgsConstructor
@Getter
public enum EcsStatusEnum {

    CREATING(0, "创建中"),
    RUNNING(1, "运行中"),
    STOPPING(2, "停止中"),
    STOPPED(3, "已停止"),
    REBOOTING(4, "重启中"),
    STARTING(5, "启动中"),
    REBUILDING(6, "重装中"),
    RESIZING(7, "更配中"),
    ERROR(8, "错误"),
    DELETING(9, "删除中"),
    //以为为自建服务器枚举状态
    HAVE_NOT_OPENED(10, "待开通"),
    HAVE_OPENED(11, "已开通"),
    CALCULATING(12, "计算中"),
    SHUT_DOWN(13, "已停机"),
    REFUNDED(14, "已退款"),
    EXPIRE(15, "已过期"),
    ;


    @EnumValue
    private final int code;

    private final String desc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }
}
