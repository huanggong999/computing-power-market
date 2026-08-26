package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/24 16:32
 */
@Data
public class PcBucketListVO {

    @Schema(description = "桶名称")
    private String bucketName;
}
