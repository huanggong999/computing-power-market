package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CreditContractDTO {


    private Long id;

    private Integer type = 1;

    /**
     * 甲方联系人
     */
    @Schema(description = "甲方联系人")
    private String clientContactPerson;

    /**
     * 甲方联系电话
     */
    @Schema(description = "甲方联系电话")
    private String clientContactPhone;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    /**
     * 甲方联系人
     */
    @Schema(description = "甲方联系人")
    private String clientContactPersonName;

    /**
     * 纸质合同
     */
    @Schema(description = "纸质合同")
    private String signUploadImg;



}
