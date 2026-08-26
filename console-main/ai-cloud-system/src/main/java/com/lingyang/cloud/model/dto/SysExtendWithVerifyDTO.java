package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SysExtendWithVerifyDTO {

    /**
     * id
     */
    @Schema(description = "id")
    private Long id;
    /**
     * 状态（1审核中，2通过，3不通过, 4 已打款）
     */
    @Schema(description = "状态（1审核中，2通过，3不通过 4 已打款）")
    private Integer status;

    /**
     * 驳回理由
     */
    @Schema(description = "驳回理由")
    private String verifyRemark;


    /**
     * 打款图片
     */
    @Schema(description = "打款图片")
    private String withImage;

}
