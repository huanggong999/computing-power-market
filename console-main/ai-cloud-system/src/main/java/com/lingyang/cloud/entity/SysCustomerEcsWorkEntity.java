package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/22 14:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer_ecs_work")
public class SysCustomerEcsWorkEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5524403389689071551L;

    @TableField("customer_id")
    @Schema(description = "客户id")
    private Long customerId;

    @TableField("work_no")
    @Schema(description = "工单编号")
    private String workNo;

    @TableField("order_id")
    @Schema(description = "订单编号")
    private Long orderId;

    @TableField("number")
    @Schema(description = "数量")
    private Integer number;
}
