package com.lingyang.cloud.model.dto.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/27 15:04
 */
@Data
public class VpnFixedInfoDTO {
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "ip")
    private String[] fix_ip;
}
