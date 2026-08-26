package com.lingyang.cloud.api.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.entity.SysCustomerVolumeEntity;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.dto.SysCustomerEcsWorkEipDTO;
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
 * @Date: 2024/11/22 15:15
 */
@Data
public class PcConsoleEcsDetailVO {

    @Schema(description = "实例id")
    private Long id;

    @Schema(description = "自建服务器工单id")
    private Long workId;

    /**
     * 订单id
     */
    @Schema(description = "订单id")
    private Long orderId;

    /**
     * 订单资源uid
     */
    @Schema(description = "订单资源uid")
    private String orderSourceUid;

    /**
     * 实例id
     */
    @Schema(description = "实例id")
    private String instanceId;

    /**
     * 规格
     */
    @Schema(description = "规格")
    private String ecsScale;

    /**
     * cpu数量
     */
    @Schema(description = "cpu数量")
    private Long cpuNumber;

    /**
     * 内存大小
     */
    @Schema(description = "内存大小")
    private Long memorySize;

    /**
     * cpu型号
     */
    @Schema(description = "cpu型号")
    private String cpuModel;

    /**
     * 显卡型号
     */
    @Schema(description = "显卡型号")
    private String gpuModel;

    /**
     * 显卡显存
     */
    @Schema(description = "显卡显存")
    private String gpuMemory;

    /**
     * 实列描述
     */
    @Schema(description = "实列描述")
    private String description;

    /**
     * 实列名称
     */
    @Schema(description = "实列名称")
    private String instanceName;

    /**
     * 主机名称
     */
    @Schema(description = "主机名称")
    private String hostName;

    /**
     * 镜像id
     */
    @Schema(description = "镜像id")
    private String imageId;
    @Schema(description = "镜像名称")
    private String osName;

    @Schema(description = "操作系统类型")
    private String osType;

    /**
     * 是否开启installRunCommandAgent
     */
    @Schema(description = "是否开启installRunCommandAgent")
    private Boolean commandAgent;

    /**
     * 客户id
     */
    @Schema(description = "客户id")
    private Long customerId;

    /**
     * 区域
     */
    @Schema(description = "区域")
    private SourceRegionsEnum region;

    /**
     * 可用区id
     */
    @Schema(description = "可用区")
    private SourceRegionZoneVO zone;

    /**
     * 计费类型：按量计费（后付费），包年包月（先付费）
     */
    @Schema(description = "计费类型")
    private SourceChargeTypeEnum chargeType;

    @Schema(description = "时长")
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @Schema(description = "时长单位")
    private SourceChargeUnitEnum durationUnit;
    /**
     * 实列状态
     */
    @Schema(description = "实列状态")
    private EcsStatusEnum status;

    /**
     * 到期时间
     */
    @Schema(description = "到期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "附加云盘信息")
    private List<SysCustomerVolumeEntity> volumeList;

    @Schema(description = "系统盘")
    private SysCustomerVolumeEntity sysVolume;

    @Schema(description = "网络信息")
    private List<NetworkInterfaceForDescribeInstancesOutput> networkList;

    @Schema(description = "公网ip信息")
    private EipAddressForDescribeInstancesOutput eipAddress;

    @Schema(description = "公网信息集合")
    private List<SysCustomerEcsWorkEipDTO> publicInfos;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;
}
