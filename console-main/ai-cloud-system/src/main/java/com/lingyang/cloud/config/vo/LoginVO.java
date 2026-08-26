package com.lingyang.cloud.config.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户登录信息
 */
@Data
public class LoginVO {

    @Schema(description = "token;以authorization放在请求头")
    private String token;

    @Schema(description = "是否需要选择身份 true 需要 false 不需要")
    private Boolean selectType;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Schema(description = "1 小程序注册， 2 会员系统推送")
    private Integer masterRegisterType;


    public void init(Long userId, String userName,  String portrait, String phone, String wxOpenId, Integer type) {
        userInfo = new UserInfo(userId, userName, portrait, phone, wxOpenId, type
        );
    }

    @Data
    public static class UserInfo {
        public UserInfo(Long userId, String userName,  String portrait, String phone, String wxOpenId, Integer type) {
            this.userId = userId;
            this.username = userName;
            this.portrait = portrait;
            this.phone = phone;
            this.wxOpenId = wxOpenId;
            this.type = type;
        }

        @Schema(description = "id")
        private Long userId;

        @Schema(description = "姓名")
        private String username;

        @Schema(description = "头像地址")
        private String portrait;

        /**
         * 手机号
         */
        @Schema(description = "手机号")
        private String phone;

        /**
         * 微信openid
         */
        @Schema(description = "微信openid")
        private String wxOpenId;

        @Schema(description = " 用户角色(1 用户 2 师傅 3 经销商)")
        private Integer type;


    }

}
