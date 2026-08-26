package com.lingyang.cloud.model.vo.invoice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 14:52
 */
@Data
public class SysInvoiceManageVO {

    @Schema(description = "发票id")
    private Long id;

    @Schema(description = "操作类型（1开票失败 2已开票）")
    private Integer type;

    /**
     * 发票备注
     */
    @Schema(description = "发票备注")
    private String remark;

    /**
     * 发票凭证
     */
    @Schema(description = "发票凭证")
    private String proof;
}
