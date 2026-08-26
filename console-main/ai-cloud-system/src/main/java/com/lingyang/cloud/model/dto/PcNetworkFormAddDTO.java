package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PcNetworkFormAddDTO {

    /**
     * 产品id
     */
    @Schema(description = "产品id")
    private Long productId;
    /**
     * json
     */
    @Schema(description = "json")
    private String json;
    /**
     * 带宽(M) 数量
     */
    @Schema(description = "带宽(M) 数量")
    private Integer bandwidth = 1;

    /**
     * 产品天数
     */
    @Schema(description = "产品天数")
    private Integer networkDay = 1;

    /**
     * 购买产品数量
     */
    @Schema(description = "购买产品数量")
    private Integer networkCount = 1;

    /**
     * 购买IP数量
     */
    @Schema(description = "购买IP数量")
    private Integer ipCount = 1;

    @Schema(description = "是否续费")
    private Boolean noOrNoXufei = false;



}
