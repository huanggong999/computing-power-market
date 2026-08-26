package com.lingyang.cloud.api.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/20 11:26
 */
@Data
public class PcUpdateCustomerNameVO {

    @Schema(description = "网络产品记录id")
    private Long networkValueId;

    @Schema(description = "客户名称")
    private String agiCustomerName;
}
