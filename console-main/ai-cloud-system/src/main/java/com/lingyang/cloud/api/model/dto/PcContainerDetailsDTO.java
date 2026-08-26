package com.lingyang.cloud.api.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 16:14
 */
@Data
public class PcContainerDetailsDTO {

    @Schema(description = "集群ID")
    private String clusterId;

    @Schema(description = "集群名称")
    private String clusterName;

    @Schema(description = "集群状态")
    private String status;

    @Schema(description = "kubernetes版本")
    private String kubernetesVersion;

    @Schema(description = "所属私有网络")
    private String vpcType;

    @Schema(description = "网络模型")
    private String networkModel;

    @Schema(description = "Pod子网")
    private String podSubnet;

    @Schema(description = "Service CIDRV4")
    private String serviceCidrV4;

    @Schema(description = "Proxy 模式")
    private String proxyMode;

    @Schema(description = "节点默认安全组")
    private String nodeSecurityGroup;

    @Schema(description = "Pod默认安全组")
    private String podSecurityGroup;


    @Schema(description = "控制面子网")
    private String controlPlaneSubnet;

    @Schema(description = "私网访问")
    private String privateAccess;

    @Schema(description = "负载均衡")
    private String loadBalancer;

    @Schema(description = "API Server 公网访问")
    private String apiServerPublicAccess;

    @Schema(description = "CPU (Core)")
    private Long cpuCore;

    @Schema(description = "内存 (GiB)")
    private Long memoryGiB;

    /**
     * nat网关
     */
    @Schema(description = "NAT网关")
    private String natName;

    @Schema(description = "实例 （个）")
    private Integer instanceNumber;

}
