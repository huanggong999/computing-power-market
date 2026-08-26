package com.lingyang.cloud.api.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/28 15:21
 */
@Data
public class PcInvoiceCompanyDTO {

    @Schema(description = "企业名称(发票抬头)")
    private String companyName;

    @Schema(description = "企业统一社会信用代码（纳税人识别号）")
    private String companyCode;

    @Schema(description = "企业注册地址")
    private String companyAddress;

    @TableField("company_contact_phone")
    @Schema(description = "企业联系人电话")
    private String companyContactPhone;
}
