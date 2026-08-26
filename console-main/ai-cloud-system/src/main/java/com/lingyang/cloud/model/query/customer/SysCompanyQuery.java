package com.lingyang.cloud.model.query.customer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lingyang.cloud.enums.system.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/30 11:14
 */
@Data
public class SysCompanyQuery {

    @Schema(description = "企业名称")
    private String companyName;


    @Schema(description = "企业审核状态（1 未提交， 2 审核中， 3 已通过，4 未通过）")
    private Integer companyStatus;
}
