package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/15 17:35
 */
@Data
@TableName("sys_customer_cert_log")
public class SysCustomerCertLogEntity implements Serializable {

    @Serial
    private static final long serialVersionUID =  9144880375116792770L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    private Long id;

    /**
     * 客户id
     */
    @TableField("customer_id")
    private Long customerId;

    @TableField("cert_type")
    @Schema(description = "认证类型（1个人认证 2企业认证）")
    private Integer certType;

    @TableField("company_name")
    @Schema(description = "企业名称")
    private String companyName;

    @TableField("company_code")
    @Schema(description = "企业统一社会信用代码")
    private String companyCode;

    @TableField("company_address")
    @Schema(description = "企业注册地址")
    private String companyAddress;


    @TableField("company_img")
    @Schema(description = "企业营业执照")
    private String companyImg;

    @TableField("name")
    @Schema(description = "名称（个人名称or法人名称）")
    private String name;

    @TableField("id_card")
    @Schema(description = "身份证号码（个人身份证号码or法人身份证号码）")
    private String idCard;

    @TableField("cert_status")
    @Schema(description = "认证状态（1未认证 2认证中 3已认证 4认证失败）")
    private Integer certStatus;

    @TableField(value = "create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @TableField(value = "update_time",fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
