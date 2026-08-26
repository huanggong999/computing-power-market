package com.lingyang.cloud.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 16:14
 */
@Data
public class PcContainerListDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "集群ID")
    private String clusterId;

    @Schema(description = "集群名称")
    private String clusterName;

    @Schema(description = "集群状态")
    private String status;

    @Schema(description = "VCI实例数")
    private Integer vciNumber;

    @Schema(description = "CPU数量")
    private Long cpuNumber;

    @Schema(description = "内存大小")
    private Long memorySize;

    @Schema(description = "kubernetes版本")
    private String kubernetesVersion;

    @Schema(description = "私网访问")
    private String privateAccess;

    @Schema(description = "API Server 公网访问")
    private String apiServerPublicAccess;

    @Schema(description = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiredTime;

}
