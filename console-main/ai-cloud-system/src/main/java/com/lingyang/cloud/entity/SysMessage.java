package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {

    @TableField("msg_type")
    @Schema(description = "消息类型(1 收到客户表单， 2 收到客户购买表达， 3 推广申请， 4 发票申请，5 合同申请，6 IP库预警，7自建服务器工单,8续费通知，9升级通知)")
    private Integer msgType;

    @TableField("text")
    @Schema(description = "消息说明")
    private String text;


    @TableField("status")
    @Schema(description = "状态（1 未读， 2 已读）")
    private Integer status;

    @TableField("user_id")
    @Schema(description = "用户id")
    private Long userId;

}
