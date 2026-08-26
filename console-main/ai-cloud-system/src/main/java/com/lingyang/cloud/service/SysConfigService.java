package com.lingyang.cloud.service;

import com.lingyang.cloud.entity.SysConfigEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:42
 */
public interface SysConfigService {
    SysConfigEntity getConfig(SystemConfigEnum config);

    boolean updateConfig(SysConfigEntity entity);
}
