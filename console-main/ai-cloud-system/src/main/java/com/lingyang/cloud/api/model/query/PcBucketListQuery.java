package com.lingyang.cloud.api.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/25 18:41
 */
@Data
public class PcBucketListQuery {
    @Schema(description = "桶名称")
    private String bucketName;
}
