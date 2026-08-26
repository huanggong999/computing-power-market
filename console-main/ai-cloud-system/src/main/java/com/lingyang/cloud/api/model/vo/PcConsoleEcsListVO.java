package com.lingyang.cloud.api.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.entity.SysCustomerVolumeEntity;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.volcengine.ecs.model.EipAddressForDescribeInstancesOutput;
import com.volcengine.ecs.model.NetworkInterfaceForDescribeInstancesOutput;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 19:57
 */
@Data
public class PcConsoleEcsListVO {
    @Schema(description = "订单资源id")
    private String orderSourceUid;

    @Schema(description = "客户实列id")
    private Long id;

    @Schema(description = "实列id")
    private String instanceId;

    @Schema(description = "实列名称")
    private String instanceName;

    @Schema(description = "实列状态")
    private EcsStatusEnum status;

    @Schema(description = "可用区")
    private SourceRegionZoneVO zone;

    @Schema(description = "镜像类型")
    private String osName;

    @Schema(description = "规格")
    private String cale;

    @Schema(description = "计费类型")
    private SourceChargeTypeEnum chargeType;

    @Schema(description = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiredTime;

    @Schema(description = "云盘信息")
    private List<SysCustomerVolumeEntity> volumeList;

    @Schema(description = "系统盘")
    private SysCustomerVolumeEntity systemVolume;

    @Schema(description = "网络信息")
    private List<NetworkInterfaceForDescribeInstancesOutput> networkList;

    @Schema(description = "公网ip信息")
    private EipAddressForDescribeInstancesOutput eipAddress;

    @Schema(description = "私网ip")
    private String primaryIpAddress;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;
}
