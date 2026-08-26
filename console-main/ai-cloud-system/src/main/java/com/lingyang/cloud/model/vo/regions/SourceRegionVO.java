package com.lingyang.cloud.model.vo.regions;

import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/1 16:45
 */
@Data
@AllArgsConstructor
public class SourceRegionVO {

    @Schema(description = "区域枚举")
    private SourceRegionsEnum regions;

    @Schema(description = "区域id")
    private String id;

    @Schema(description = "区域名称")
    private String name;
}
