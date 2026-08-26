package com.lingyang.cloud.model.vo.product;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/7/16 15:08
 */
@Data
public class SysProductIpExportVO {

    @Schema(description = "产品ID")
    private Long productId;

    @Schema(description = "IPID集合")
    private List<Long> ids;

    /**
     * 使用状态（0未使用 1已使用）
     */
    @Schema(description = "使用状态（0未使用 1已使用）")
    private Integer status;

    @Schema(description = "内网IP")
    private String ip;

    @Schema(description = "公网IP")
    private String publicIp;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "客户账号")
    private String customerName;
}
