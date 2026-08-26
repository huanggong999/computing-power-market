package com.lingyang.cloud.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/4 15:51
 */
@Data
public class SysProductIpEnoughVO {

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "ip数量")
    private Integer ipNumber;
}
