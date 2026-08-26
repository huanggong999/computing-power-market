package com.lingyang.cloud.api.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/25 16:23
 */
@Data
public class PcBucketDetailDTO {

    @Schema(description = "桶对象数量")
    private Integer bucketObjectCount;

    @Schema(description = "桶对象总容量")
    private BigDecimal bucketObjectSize;

    @Schema(description = "桶名称")
    private String name;

    @Schema(description = "桶策略（0私有 1公共读 2公共读写）")
    private Integer bucketStrategy;

    /**
     * 存储类型（默认=标准存储）
     */
    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "冗余类型（0单冗余 1多AZ冗余）")
    private Integer redundancyType;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "外网地域节点")
    private String extranetEndpoint;

    @Schema(description = "内网地域节点")
    private String intranetEndpoint;

    @Schema(description = "外网S3地域节点")
    private String extranetS3Endpoint;

    @Schema(description = "内网S3地域节点")
    private String intranetS3Endpoint;

    @Schema(description = "外网bucket域名")
    private String extranetDomain;

    @Schema(description = "内网bucket域名")
    private String intranetDomain;

    @Schema(description = "地域")
    private String region;

}
