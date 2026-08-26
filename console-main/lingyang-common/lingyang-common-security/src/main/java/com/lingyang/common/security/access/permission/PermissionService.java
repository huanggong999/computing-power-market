package com.lingyang.common.security.access.permission;

import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.security.model.Permission;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/14 17:23
 */
public class PermissionService{

    /**
     * 当前用户是否拥有权限
     *
     * @param permission 权限字符
     * @return true/false
     */
    public boolean hasPermission(String permission) {
        if (ObjectUtils.isEmpty(permission)) {
            return true;
        }
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        if (SecurityContext.isAdmin()) {
            return true;
        }
        Collection<? extends Permission> permissions = userInfo.getPermissions();
        if (ObjectUtils.isEmpty(permissions)) {
            return false;
        }
        return permissions.stream().anyMatch(a -> a.getAuthority().equals(permission));
    }
}