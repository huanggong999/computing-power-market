package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysRemitVerifyDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "状态（1 待核实， 2 已打款， 3 未打款）")
    private Integer status;

    @Schema(description = "审核说明")
    private String remark;
}
