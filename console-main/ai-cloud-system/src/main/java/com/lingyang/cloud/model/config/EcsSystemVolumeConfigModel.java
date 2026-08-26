package com.lingyang.cloud.model.config;

import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.SysOrderSourceEntity;
import com.lingyang.cloud.entity.SysPriceEntity;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/6 17:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EcsSystemVolumeConfigModel extends SysPriceEntity {
    @Serial
    private static final long serialVersionUID = 2040648470001638299L;

    @Schema(description = "磁盘类型，固定system")
    private final String kind = "system";

    /**
     * 云盘大小 40-2048（GiB）
     */
    @Schema(description = "云盘大小 40-2048（GiB）")
    @Size(min = 40, max = 2048)
    private Integer size = 40;

    /**
     * 云盘类型
     * ESSD_PL0（默认）：极速型SSD PL0
     * ESSD_FlexPL：极速型SSD FlexPL
     */
    @Schema(description = """
            云盘类型
            ESSD_PL0（默认）：极速型SSD PL0
            ESSD_FlexPL：极速型SSD FlexPL
            """)
    private String volumeType = "ESSD_FlexPL";

    /**
     * 性能级别 没啥用
     */
    @Schema(description = "性能级别 没啥用")
    private String performanceLevel = "FlexPL(单盘IOPS基准性能上限5万)";

    @Schema(description = "iops")
    private Integer iops = 2280;

    @Schema(description = "吞吐量")
    private Integer throughput = 110;


    /**
     * 按量计费
     */
    @Schema(description = "按量计费")
    private BigDecimal hoursPrice = BigDecimal.valueOf(0.0021);

    /**
     * 包年包月
     */
    @Schema(description = "包年包月")
    private BigDecimal monthPrice = BigDecimal.valueOf(1);

    /**
     * 1年
     */
    @Schema(description = "1年")
    private BigDecimal oneYearPrice = BigDecimal.valueOf(9.96);

    /**
     * 2年
     */
    @Schema(description = "2年")
    private BigDecimal twoYearPrice = BigDecimal.valueOf(16.8);

    /**
     * 3年
     */
    @Schema(description = "3年")
    private BigDecimal threeYearPrice = BigDecimal.valueOf(19.8);

    public static SysOrderSourceEntity buildOrderSource(SysOrderSourceEntity ecsOrderSource, EcsSystemVolumeConfigModel configModel) {
        SysOrderSourceEntity orderSourceEntity = new SysOrderSourceEntity();
        orderSourceEntity.setRegionsId(ecsOrderSource.getRegionsId());
        orderSourceEntity.setSourceType(SourceTypeEnum.CLOUD_STORAGE);
        orderSourceEntity.setSourceName(configModel.getVolumeType());
        orderSourceEntity.setProductName("系统盘");
        orderSourceEntity.setConfigDetail(JSONObject.parseObject(JSONObject.toJSONString(configModel)));
        orderSourceEntity.setChargeType(ecsOrderSource.getChargeType());
        orderSourceEntity.setDuration(ecsOrderSource.getDuration());
        orderSourceEntity.setDurationUnit(ecsOrderSource.getDurationUnit());
        return orderSourceEntity;
    }
}