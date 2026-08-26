package com.lingyang.cloud.model.vo.product;

import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/17 10:24
 */
@Data
public class SysProductUpgradeVO {

    @Schema(description = "记录id")
    private Long valueId;

    @Schema(description = "增加带宽数量（M）")
    private Integer bandwidth;

    @Schema(description = "增加IP数量")
    private Integer ipCount;

    @Schema(description = "增加账户数量")
    private Integer networkCount;

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
