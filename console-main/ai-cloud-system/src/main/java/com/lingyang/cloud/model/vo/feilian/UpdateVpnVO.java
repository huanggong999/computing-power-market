package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 14:38
 */
@Data
public class UpdateVpnVO {

    @Schema(description = "VPN授权配置ID列表")
    private Integer[] ids;

    @Schema(description = "使用时长（单位天），默认0，表示永久有效")
    private Integer days;
}
