package com.lingyang.cloud.client;

import com.lingyang.cloud.common.login.model.PcLoginUserInfo;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Provides the GPU scheduler tenant that belongs to the currently logged-in customer.
 */
@Component
public class GpuPodTenantProvider {

    public Tenant getCurrentTenant() {
        LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        String tenantId = String.valueOf(userInfo.getUserId());
        String tenantName = userInfo instanceof PcLoginUserInfo pcUser
                ? pcUser.getCustomerName()
                : userInfo.getUsername();
        return new Tenant(tenantId, StringUtils.defaultIfBlank(tenantName, tenantId));
    }

    public record Tenant(String tenantId, String tenantName) {
    }
}
