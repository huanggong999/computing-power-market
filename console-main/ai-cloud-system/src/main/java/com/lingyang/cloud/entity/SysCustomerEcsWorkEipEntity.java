package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/5/22 18:02
 */
@Data
@TableName("sys_customer_ece_work_eip")
public class SysCustomerEcsWorkEipEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5524403389689071551L;

    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    @TableField("instance_id")
    @Schema(description = "实例id")
    private Long instanceId;

    @TableField("ecs_work_id")
    @Schema(description = "服务器工单id")
    private Long ecsWorkId;

    @TableField("public_ip")
    @Schema(description = "公网ip")
    private String publicIp;

    @TableField("private_ip")
    @Schema(description = "私网ip")
    private String privateIp;

    @TableField("port")
    @Schema(description = "端口")
    private String port;

    @TableField("access_method")
    @Schema(description = "访问方式")
    private String accessMethod;

    @TableField("user_status")
    @Schema(description = "用户控制状态（1申请开通 2申请停机 3申请重启 4申请续费 5申请销毁）")
    private Integer userStatus;

    @TableField("operation_status")
    @Schema(description = "运维状态（1待开通 2已开通 3计算中 4已停机 5已退款 6已过期）")
    private Integer operationStatus;
}
