package com.lingyang.cloud.service.impl;

import com.lingyang.cloud.entity.SysConfigEntity;
import com.lingyang.cloud.enums.system.SystemConfigEnum;
import com.lingyang.cloud.mapper.SysConfigMapper;
import com.lingyang.cloud.service.SysConfigService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:43
 */
@Service
public class SysConfigServiceImpl implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;

    @Override
    public SysConfigEntity getConfig(SystemConfigEnum config) {
        SysConfigEntity configEntity = sysConfigMapper.selectById(config.getCode());
        if (ObjectUtils.isEmpty(configEntity)) {
            configEntity = new SysConfigEntity();
            configEntity.setId(config.getCode());
            configEntity.setConfigKey(config.name());
            configEntity.setConfigValue(config.getDefaultValue());
            sysConfigMapper.insert(configEntity);
        }
        return configEntity;
    }

    @Override
    public boolean updateConfig(SysConfigEntity entity) {
        return sysConfigMapper.updateById(entity) > 0;
    }
}
