package com.lingyang.cloud.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysInvoiceEmailEntity;
import com.lingyang.cloud.mapper.SysInvoiceEmailMapper;
import com.lingyang.cloud.service.SysInvoiceEmailService;
import com.lingyang.common.core.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 吴思镇
 */
@Service
public class SysInvoiceEmailServiceImpl implements SysInvoiceEmailService {

    @Autowired
    private SysInvoiceEmailMapper sysInvoiceEmailMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean setDefault(SysInvoiceEmailEntity entity) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        LambdaQueryWrapper<SysInvoiceEmailEntity> queryWrapper = Wrappers.lambdaQuery(SysInvoiceEmailEntity.class)
                .eq(SysInvoiceEmailEntity::getCustomerId, userId)
                .eq(SysInvoiceEmailEntity::getIsDefault, 1);
        SysInvoiceEmailEntity children = sysInvoiceEmailMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(children)) {
            children.setIsDefault(0);
            sysInvoiceEmailMapper.updateById(children);
        }
        entity.setIsDefault(1);
        sysInvoiceEmailMapper.updateById(entity);
        return true;
    }
}
