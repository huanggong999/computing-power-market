package com.lingyang.cloud.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_remit")
public class SysRemit extends BaseEntity {

    private Long userId;


    @Schema(description = "银行类型（1 国内， 2 国外）")
    private Integer bankType;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "开户名称")
    private String account;
    @Schema(description = "银行卡号")
    private String bankNo;
    @Schema(description = "SWIFT CODE")
    private String swiftCode;
    @Schema(description = "打款金额")
    private BigDecimal amount;
    @Schema(description = "汇款主体名")
    private String remitName;
    @Schema(description = "状态（1 待核实， 2 已打款， 3 未打款）")
    private Integer status;

    @Schema(description = "审核说明")
    private String remark;


    @TableField(exist = false)
    private String customerName;

    @TableField(exist = false)
    private String phone;


}
