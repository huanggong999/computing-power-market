package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysExtendVerifyDTO {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;

    /**
     * 状态(2通过，3不通过）
     */
    @Schema(description = "状态(2通过，3不通过）")
    private Integer status;

    /**
     * 驳回理由
     */
    @Schema(description = "驳回理由")
    private String verifyRemark;

}
