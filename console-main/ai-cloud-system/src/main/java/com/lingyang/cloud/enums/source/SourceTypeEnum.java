package com.lingyang.cloud.enums.source;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 16:49
 */
@AllArgsConstructor
@Getter
public enum SourceTypeEnum {
    ECS(1, "服务器", "ECS"),
    // 弹性存储
    CLOUD_STORAGE(2, "云存储", "volume"),
    // 镜像
    CLOUD_IMAGE(3, "云镜像", "IMS"),
    // 对象存储
    CLOUD_OBJECT_STORAGE(4, "对象存储", ""),
    // 网络
    CLOUD_NETWORK(5, "网络","EIP"),
    // 容器
    CONTAINER(6, "容器","CONTAINER"),//todo 待定，需要查询账单的时候才显示
    // AGIC
    AGIC(7, "AGIC","AGIC"),
    // 负载均衡
    CLB(8, "负载均衡","CLB"),
    // NAT网关
    NAT_GATEWAY(9, "NAT网关","NAT_Gateway"),
    // 容器服务托管
    VKE(10, "容器服务托管","VKE"),
    // 镜像仓库
    CR(11, "镜像仓库","cr"),
    // 授信额充值
    CREDIT_AMOUNT_RECHARGE(12, "授信额充值","CREDIT_AMOUNT_RECHARGE"),
    GPU_SERVER(13, "GPU服务器","GPU_Server"),
    ;

    @EnumValue
    private final int id;


    private final String desc;
    private final String volcengineBillDesc;

    @Override
    public String toString() {
        return name() + " = " + desc;
    }

    public static SourceTypeEnum getByVolcengineBillDesc(String volcengineBillDesc) {
        for (SourceTypeEnum value : SourceTypeEnum.values()) {
            if (value.getVolcengineBillDesc().equals(volcengineBillDesc)) {
                return value;
            }
        }
        return null;
    }
}
