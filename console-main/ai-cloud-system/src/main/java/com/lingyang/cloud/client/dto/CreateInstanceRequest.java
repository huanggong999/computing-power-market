package com.lingyang.cloud.client.dto;

import lombok.Data;

/**
 * 创建 GPU Pod 实例请求
 */
@Data
public class CreateInstanceRequest {

    /**
     * 租户 ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * GPU 规格
     */
    private GpuSpec gpuSpec;

    /**
     * 镜像名称
     */
    private String image;

    /**
     * 计费信息
     */
    private Billing billing;

    /**
     * 资源配置
     */
    private Resource resource;

    /**
     * Pod 名称
     */
    private String podName;

    /**
     * 区域
     */
    private String region;

    /**
     * GPU 规格
     */
    @Data
    public static class GpuSpec {
        /**
         * GPU 型号
         */
        private String model;

        /**
         * GPU 数量
         */
        private int count;
    }

    /**
     * 计费信息
     */
    @Data
    public static class Billing {
        /**
         * 计费模式：hourly（按小时）、monthly（包月）
         */
        private String mode;

        /**
         * 购买时长（小时或月）
         */
        private Integer duration;
    }

    /**
     * 资源配置
     */
    @Data
    public static class Resource {
        /**
         * CPU 规格
         */
        private String cpu;

        /**
         * 内存规格
         */
        private String memory;

        /**
         * 系统盘规格
         */
        private String systemDisk;

        /**
         * 数据盘规格
         */
        private String dataDisk;
    }
}
