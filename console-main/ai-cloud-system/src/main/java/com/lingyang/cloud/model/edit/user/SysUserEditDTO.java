package com.lingyang.cloud.model.edit.user;


import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.enums.system.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/29 10:27
 */
@Data
public class SysUserEditDTO {

    @Schema(description = "用户id")
    private Long id;
    /**
     * 用户账号
     */
    @Schema(description = "用户名")
    @NotEmpty(message = "用户名为空")
    private String userName;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickName;

    /**
     * 手机号码
     */
    @Schema(description = "手机号码")
    private String phone;

    /**
     * 用户性别（0男 1女 2未知）
     */
    @Schema(description = "用户性别")
    private SexEnum sex;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    private String avatar;

    /**
     * 密码
     */
    @Schema(description = "密码")
    @NotEmpty(message = "密码为空")
    private String password;

    /**
     * 帐号状态
     */
    @Schema(description = "帐号状态")
    @NotNull(message = "帐号状态为空")
    private StatusEnum status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;

    @Schema(description = "角色id")
    @NotNull(message = "角色为空")
    private Long roleId;
}
