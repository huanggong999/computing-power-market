package com.lingyang.cloud.model.query.home;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class SysProductIpQuery {

    @Schema(description = "AGI-C产品id")
    private Long productId;

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
