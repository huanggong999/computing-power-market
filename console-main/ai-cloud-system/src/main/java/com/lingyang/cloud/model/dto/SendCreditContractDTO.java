package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendCreditContractDTO {

    @Schema(description = "合同id")
    private Long id;

    @Schema(description = "合同金额")
    private BigDecimal amount;

    /**
     * 账期
     */
    @Schema(description = "账期")
    private Integer zq;


    /**
     * 签署扫描件
     */
    @Schema(description = "签署扫描件")
    private String uploadImg;


    @Schema(description = "纸质合同(1 待乙方确认，2 待归档， 3 已签署， 5 审核不通过，6 归档审核不通过)")
    private Integer status;

    @Schema(description = "审核备注")
    private String verifyRemark;



}
