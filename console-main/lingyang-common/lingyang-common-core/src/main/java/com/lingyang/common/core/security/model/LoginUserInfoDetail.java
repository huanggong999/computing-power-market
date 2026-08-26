package com.lingyang.common.core.security.model;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lingyang.common.core.model.BaseRole;
import com.lingyang.common.core.utils.Optional;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/21 11:04
 */
@Data
public class LoginUserInfoDetail implements UserDetails {
    @Serial
    private static final long serialVersionUID = -35387052512616528L;
//
//    /**
//     * 请求来源
//     */
//    private RequestSource source;
//    /**
//     * tokenKey
//     */
//    private String tokenKey;

    /**
     * 用户id
     */
    @JSONField(serializeFeatures = JSONWriter.Feature.WriteLongAsString)
    @Schema(description = "用户id")
    private Long userId;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    @Schema(description = "是否为超管用户")
    private boolean superAdmin = false;

    /**
     * 用户角色列表
     */
    @Schema(description = "用户角色列表")
    private Collection<? extends BaseRole> roleList;

    @Schema(description = "菜单id列表")
    private Collection<Long> menuIdList;
    /**
     * 权限字符集
     */
    @Schema(description = "权限字符集")
    private Collection<? extends Permission> permissions;

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return permissions;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    @Override
    public int hashCode() {
        return this.userId.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof LoginUserInfoDetail loginUserInfoDetail) {
          return loginUserInfoDetail.getUserId().equals(this.userId);
      }
      return false;
    }

    public void setRoleList(Collection<? extends BaseRole> roleList) {
        Optional.of(roleList)
                .ifPresent( roles -> {
                    for (BaseRole role : roles) {
                        if (role.isAdmin()) {
                            this.superAdmin = true;
                        }
                    }
                });
        this.roleList = roleList;
    }
}
