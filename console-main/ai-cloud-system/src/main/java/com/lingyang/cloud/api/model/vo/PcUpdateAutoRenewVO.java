package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/20 11:26
 */
@Data
public class PcUpdateAutoRenewVO {

    @Schema(description = "网络产品记录id")
    private Long networkValueId;

    @Schema(description = "是否自动续费（0否 1是）")
    private Integer isAutoRenew;
}
