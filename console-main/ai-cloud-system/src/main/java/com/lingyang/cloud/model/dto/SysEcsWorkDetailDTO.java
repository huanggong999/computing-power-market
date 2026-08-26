package com.lingyang.cloud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.cloud.enums.source.EcsTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/22 17:41
 */
@Data
public class SysEcsWorkDetailDTO {

    @Schema(description = "工单id")
    private Long id;

    @Schema(description = "订单id")
    private Long orderId;

    @Schema(description = "工单编号")
    private String workNo;

    @Schema(description = "订单编号")
    private String orderNo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private Date createTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "已使用时间（天）")
    private Integer usedTime;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户姓名")
    private String customerName;

    @Schema(description = "用户手机号")
    private String phone;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;

    @Schema(description = "区域")
    private SourceRegionsEnum region;

    @Schema(description = "可用区")
    private SourceRegionZoneVO zone;

    @Schema(description = "服务器类型")
    private EcsTypeEnum ecsTypeEnum;

    @Schema(description = "规格")
    private String ecsScale;

    @Schema(description = "内存大小")
    private Long memorySize;

    @Schema(description = "cpu型号")
    private String cpuModel;

    @Schema(description = "显卡型号")
    private String gpuModel;

    @Schema(description = "显卡显存")
    private String gpuMemory;

    @Schema(description = "镜像ID")
    private String imageId;

    @Schema(description = "镜像类型(public：公共镜像  private：自定义镜像 shared：共享镜像)")
    private String imageType;

    @Schema(description = "镜像品牌")
    private String imageBrand;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "系统盘")
    private String systemDisk;

    @Schema(description = "数据盘")
    private String dataDisk;

    @Schema(description = "系统盘容量")
    private Integer systemDiskSize;

    @Schema(description = "数据盘容量")
    private Integer dataDiskSize;

    @Schema(description = "是否分配公网")
    private Boolean isAllocatePublicIp;


    @Schema(description = "登录名")
    private String loginName;

    @Schema(description = "登录密码")
    private String loginPassword;

    @Schema(description = "链接地址")
    private String linkAddress;

    @Schema(description = "计费类型")
    private SourceChargeTypeEnum chargeType;

    @Schema(description = "时长")
    private Integer duration;

    @Schema(description = "时长单位")
    private SourceChargeUnitEnum durationUnit;

    @Schema(description = "开通数量")
    private Integer quantity;

    @Schema(description = "实例状态集合")
    private List<SysEcsWorkInstancesDTO> instanceStatusList;
}
