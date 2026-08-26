package com.lingyang.cloud.model.edit.order;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/12 17:48
 */
@Data
public class SysOrderSourceCreateDTO {
    @Schema(description = "uid，区别多个服务器下单，一台服务器必须有系统盘，网络，uid一致前端生成")
    private String uid;
    /**
     * 地区id
     */
    @Schema(description = "地区")
    private SourceRegionsEnum regionsId;

    @Schema(description = "资源类型")
    private SourceTypeEnum sourceType;

    /**
     * 产品类型（1火山云引擎 2自建服务器）
     */
    @Schema(description = "产品类型（1火山云引擎 2自建服务器）")
    private Integer productType;

    /**
     * 配置详情
     */
    @Schema(description = "配置详情， 为各个配置的列表参数")
    private JSONObject configDetail;

    /**
     * 计费类型：按量计费（后付费），包年包月（先付费）
     */
    @Schema(description = "计费类型")
    private SourceChargeTypeEnum chargeType;

    /**
     * 时长
     */
    @Schema(description = "时长")
    private Integer duration;

    /**
     * 时长单位， 按量计费 小时/GB， 包年 年， 包月 月
     */
    @Schema(description = "时长单位")
    private SourceChargeUnitEnum durationUnit;
}