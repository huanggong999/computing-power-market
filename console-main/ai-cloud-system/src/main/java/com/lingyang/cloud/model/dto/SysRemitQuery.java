package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysRemitQuery {

    @Schema(description = "汇款主体名")
    private String remitName;
    @Schema(description = "状态（1 待核实， 2 已打款， 3 未打款）")
    private Integer status;

    private Long userId;

}
