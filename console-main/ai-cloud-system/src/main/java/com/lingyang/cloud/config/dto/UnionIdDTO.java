package com.lingyang.cloud.config.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 小程序登录参数
 * @author Administrator
 */
@Data
@Builder
public class UnionIdDTO {

    @Schema(description = "unionId")
    private String unionId;

    @Schema(description = "openId")
    private String openId;

    @Schema(description = "是否绑定标识")
    private Boolean isBind;
}
