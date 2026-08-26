package com.lingyang.cloud.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/8/18 17:35
 */
@Data
public class TokenDTO {

    @Schema(description = "token")
    private String token;

    @Schema(description = "是否有账号")
    private Boolean isExist;
}
