package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 14:12
 */
@Data
@TableName("sys_active_record")
public class SysActiveRecordEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6459891529580738182L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "活动id")
    @NotNull(message = "活动id不能为空")
    @TableField("active_id")
    private Long activeId;

    /**
     * 分享的用户id
     */
    @Schema(description = "分享的用户id")
    @NotNull(message = "分享的用户id不能为空")
    @TableField("share_user_id")
    private Long shareUserId;

    /**
     * 分享的用户姓名
     */
    @Schema(description = "分享的用户姓名")
    @NotNull(message = "分享的用户姓名不能为空")
    @TableField("share_username")
    private String shareUsername;

    /**
     * 分享的用户手机号
     */
    @Schema(description = "分享的用户手机号")
    @TableField("share_user_phone")
    private String shareUserPhone;

    /**
     * 注册的用户id
     */
    @Schema(description = "注册的用户id")
    @NotNull(message = "注册的用户id不能为空")
    @TableField("register_user_id")
    private Long registerUserId;

    /**
     * 注册的用户姓名
     */
    @Schema(description = "注册的用户姓名")
    @NotNull(message = "注册的用户姓名不能为空")
    @TableField("register_username")
    private String registerUsername;

    /**
     * 注册的用户手机号
     */
    @Schema(description = "注册的用户手机号")
    @TableField("register_user_phone")
    private String registerUserPhone;

    @Schema(description = "邀请时间")
    @TableField("invitation_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date invitationTime;

    @Schema(description = "关联订单号")
    @TableField("order_no")
    private String orderNo;
}
