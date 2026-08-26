package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/24 16:32
 */
@Data
public class PcBucketCreateVO {

    @Schema(description = "桶名称")
    private String name;

    @Schema(description = "桶策略（0私有 1公共读 2公共读写）")
    private Integer bucketStrategy;

    @Schema(description = "冗余类型（0单冗余 1多AZ冗余）")
    private Integer redundancyType;

    @Schema(description = "是否开启版本控制")
    private Boolean isVersion;
}
