package com.lingyang.cloud.api.model.query;

import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/19 19:56
 */
@Data
public class PcConsoleEcsQuery {

    @Schema(description = "地域")
    private SourceRegionsEnum regions;

    @Schema(description = "地域类型")
    private Integer type;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "实例名称")
    private String instanceName;

    /**
     * 计费方式
     */
    @Schema(description = "计费方式")
    private SourceChargeTypeEnum chargeType;
}
