package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/02/13 13:52
 */
@AllArgsConstructor
@Getter
public enum ContainerStatusEnum {

    CREATING(0, "创建中","Creating"),
    RUNNING(1, "运行中","Running"),
    STOPPED(2, "已停止","Stopped"),
    RESIZING(3, "更配中","Updating"),
    ERROR(4, "错误","Failed"),
    DELETING(5, "删除中","Deleting")
    ;


    @EnumValue
    private final int code;

    private final String desc;

    private final String volcengineDesc;
    @Override
    public String toString() {
        return name() + " = " + desc;
    }

    public static ContainerStatusEnum getDescByVolcengineDesc(String VolcengineDesc) {
        for (ContainerStatusEnum type : ContainerStatusEnum.values()) {
            if (type.volcengineDesc.equals(VolcengineDesc)) {
                return type;
            }
        }
        return null;
    }
}
