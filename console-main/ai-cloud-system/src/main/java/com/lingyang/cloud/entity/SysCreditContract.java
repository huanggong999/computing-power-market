package com.lingyang.cloud.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_credit_contract")
public class SysCreditContract  extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 合同类型（1 线上 2 线下）
     */
    @TableField("type")
    private Integer type;

    /**
     * 签署扫描件
     */
    @Schema(description = "签署扫描件")
    private String uploadImg;

    /**
     * 已签署扫描件
     */
    @Schema(description = "已签署扫描件")
    private String signUploadImg;


    /**
     * 用户类型（1 个人  2 企业）
     */
    private Integer userType;

    /**
     * 合同编号
     */
    @TableField("contract_no")
    private String contractNo;


    /**
     * 完成时间
     */
    @TableField(value = "complete_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "完成时间")
    private Date completeTime;


    /**
     * 关联订单号
     */
    @TableField("link_order_no")
    private String linkOrderNo;


    /**
     * 电子合同(1待沟通, 2 待签署，3 已签署， 4 已过期， 6 签署中, 7 已撤回)
     * 纸质合同(1 待乙方确认，2 待归档， 3 已签署， 5 审核不通过，6 归档审核不通过)
     */
    @Schema(description = " 电子合同(1待沟通, 2 待签署，3 已签署， 4 已过期， 6 签署中, 7 已撤回)" +
            "纸质合同(1 待乙方确认，2 待归档， 3 已签署， 5 审核不通过，6 归档审核不通过, 8 待甲方上传)")
    @TableField("status")
    private Integer status;

    /**
     * 审核备注
     */
    @Schema(description = "审核备注")
    private String verifyRemark;

    /**
     * 备注
     */
    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    /**
     * 签署金额
     */
    @Schema(description = "签署金额")
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 账期
     */
    @Schema(description = "账期")
    private Integer zq;

    /**
     * 甲方名称
     */
    @Schema(description = "甲方名称")
    @TableField("client_contact_person")
    private String clientContactPerson;

    /**
     * 甲方联系人
     */
    @Schema(description = "甲方联系人")
    @TableField("client_contact_person_name")
    private String clientContactPersonName;

    /**
     * 甲方联系电话
     */
    @Schema(description = "甲方联系电话")
    @TableField("client_contact_phone")
    private String clientContactPhone;

    /**
     * 填充后生成的文件ID
     */
    private String fileId;


    /**
     * 下载文件地址
     */
    private String fileDownloadUrl;

    /**
     * signFlowId
     */
    private String signFlowId;

    /**
     * 签署链接
     */
    private String signUrl;


    private Date downloadTime;



    @TableField(exist = false)
    private String customerName;


    @TableField(exist = false)
    private String customerPhone;



}


