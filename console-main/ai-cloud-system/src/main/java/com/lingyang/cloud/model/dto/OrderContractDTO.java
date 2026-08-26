package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class OrderContractDTO {


    private Long id;


    @Schema(description = "orderIds")
    private List<Long> orderIds;


    private Integer type = 1;

    /**
     * 甲方名称
     */
    @Schema(description = "甲方名称")
    private String clientName;
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
     * 甲方联系地址
     */
    @Schema(description = "甲方联系地址")
    private String clientContactAddress;

    /**
     * 纸质合同
     */
    @Schema(description = "纸质合同")
    private String signUploadImg;



}
