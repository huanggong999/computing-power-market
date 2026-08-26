package com.lingyang.common.core.security.model;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.common.core.utils.StringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/21 10:49
 */
@Data
public class Permission implements GrantedAuthority {

    @Serial
    private static final long serialVersionUID = 4447626040752164965L;

    @Schema(description = "权限字符标识")
    private String permission;

    public Permission(String permission) {
        StringUtils.requireNonNull(permission, "permission is null");
        this.permission = permission;
    }

    @Override
    @JsonIgnore
    @JSONField(serialize = false)
    public String getAuthority() {
        return getPermission();
    }
}
