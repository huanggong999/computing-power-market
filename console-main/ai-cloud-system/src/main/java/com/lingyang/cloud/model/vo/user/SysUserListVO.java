package com.lingyang.cloud.model.vo.user;


import com.lingyang.cloud.enums.system.StatusEnum;
import com.lingyang.cloud.enums.system.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/29 10:28
 */
@Data
public class SysUserListVO {

    @Schema(description = "用户id")
    private Long id;
    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
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
     * 帐号状态（0正常 1停用,2离职）
     */
    @Schema(description = "帐号状态")
    private StatusEnum status;

    /**
     * 备注
     */
    @Schema(description = "备注")
    private String remark;
}
