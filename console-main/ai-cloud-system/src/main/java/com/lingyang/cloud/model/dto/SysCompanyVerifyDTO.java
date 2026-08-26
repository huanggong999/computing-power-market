package com.lingyang.cloud.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:14
 */
@Data
public class SysCompanyVerifyDTO {

    private Long id;


    @Schema(description = "企业审核状态（1 未提交， 2 审核中， 3 已通过，4 未通过）")
    private Integer companyStatus;

    @Schema(description = "企业审核备注")
    private String companyVerifyRemark;

}
