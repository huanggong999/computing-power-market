package com.lingyang.cloud.model.query.volume;

import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/5 17:36
 */
@Data
public class SysVolumeQuery {

    @Schema(description = "云盘类型，取值说明如下：" +
            "PTSSD：性能型SSD云盘。 " +
            "ESSD_PL0：极速型SSD云盘，PL0规格。" +
            "ESSD_FlexPL: 极速型SSD云盘，FlexPL规格")
    private String volumeType;
    @Schema(description = "可用区")
    private SourceRegionsEnum region;

    @Schema(description = "容量，不同容量对应的iops和吞吐量不一样")
    @Min(value = 20, message = "容量不能小于20GB")
    @Max(value = 32768, message = "容量不能大于32768GB")
    private Integer capacity;
}
