package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

/**
 * 创建 GPU Pod 响应（对接外部 GPU 调度服务 /api/v1/gpu/pod/create）
 */
@Data
public class GpuPodCreateResponse {

    /** 兼容部分网关返回的 {status, body} 包装结构。 */
    private GpuPodCreateResponse body;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * Pod 名称
     */
    @JsonAlias("pod_name")
    private String podName;

    /**
     * Pod 命名空间
     */
    @JsonAlias("pod_namespace")
    private String podNamespace;

    /**
     * 消息
     */
    private String message;

    /**
     * 实例 ID
     */
    @JsonAlias("instance_id")
    private String instanceId;

    /**
     * GPU 信息
     */
    @JsonAlias("gpu_info")
    private GpuInfo gpuInfo;

    /**
     * 计费信息
     */
    @JsonAlias({"billing_info", "pricing"})
    private BillingInfo billingInfo;

    /**
     * SSH 连接信息
     */
    @JsonAlias("ssh_info")
    private SshInfo sshInfo;

    @Data
    public static class GpuInfo {
        private String model;
        private int count;
    }

    @Data
    public static class BillingInfo {
        @JsonAlias("gpu_model")
        private String gpuModel;
        @JsonAlias("gpu_count")
        private int gpuCount;
        @JsonAlias("billing_mode")
        private String billingMode;
        @JsonAlias("unit_price")
        private double unitPrice;
        @JsonAlias("discount_unit_price")
        private double discountUnitPrice;
        @JsonAlias("total_cost")
        private double totalCost;
        @JsonAlias("discount_total_cost")
        private double discountTotalCost;
        private String currency;
        private String unit;
    }

    @Data
    public static class SshInfo {
        private String host;
        private Integer port;
        private String username;
        private String password;
        private String command;
    }
}
