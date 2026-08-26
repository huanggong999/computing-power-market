package com.lingyang.cloud.model.vo.feilian;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/6/11 11:42
 */
@Data
public class CreateUserVO {

    @Schema(description = "用户姓名")
    private String fullName;

    @Schema(description = "部门id")
    private String departmentId;

    @Schema(description = "手机号，与邮箱不能同时为空")
    private String mobile;

    @Schema(description = "邮箱，与手机号不能同时为空")
    private String email;

    @Schema(description = "账号生效日期，默认为现在, 格式为:2006-01-02")
    private String createDate;

    @Schema(description = "账号失效日期，默认不过期, 格式为:2006-01-02")
    private String expireDate;

    @Schema(description = "自定义入职日期，默认值为空，格式为:2006-01-02")
    private String hireDate;

    @Schema(description = "邀请类型: 0-暂不发送; 1-添加完立即发送短信邀请; 2-入职日早8点发送短信邀请；3-添加完立即发送邮件邀请; 4-入职日早8点发送邮件邀请；")
    private Integer inviteType;

    @Schema(description = "登录是否需要二次认证,默认false")
    private Boolean needMfa;

    @Schema(description = "角色id，格式为：or_xxx")
    private String roleIds;

    @Schema(description = "自定义用户id")
    private String userId;

    @Schema(description = "自定义密码，最小密码长度由管理后台配置决定")
    private String password;

    @Schema(description = "自定义密码类型，1:初始密码2:长期密码，默认值为1")
    private Integer passwordType;

    @Schema(description = "自定义头像URL地址，用户默认头像为：default_user_avatar")
    private String avatar;

}
