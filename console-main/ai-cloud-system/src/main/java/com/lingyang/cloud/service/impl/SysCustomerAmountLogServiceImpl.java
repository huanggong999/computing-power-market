package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerAmountLogEntity;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.mapper.SysCustomerAmountLogMapper;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.service.SysCustomerAmountLogService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/12/3 18:41
 */
@Service
public class SysCustomerAmountLogServiceImpl implements SysCustomerAmountLogService {
    @Resource
    private SysCustomerAmountLogMapper sysCustomerAmountLogMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Override
    public PageResult<SysCustomerAmountLogEntity> getPage(PageQuery<SysCustomerAmountLogEntity> pageQuery) {
        pageQuery.startPage();
        SysCustomerAmountLogEntity query = pageQuery.getQuery();
        LambdaQueryWrapper<SysCustomerAmountLogEntity> queryWrapper = Wrappers.lambdaQuery(SysCustomerAmountLogEntity.class)
                .eq(query.getCustomerId() != null, SysCustomerAmountLogEntity::getCustomerId, query.getCustomerId())
                .eq(query.getAmountType() != null, SysCustomerAmountLogEntity::getAmountType, query.getAmountType())
                .orderByDesc(SysCustomerAmountLogEntity::getCreateTime);
        List<SysCustomerAmountLogEntity> data = sysCustomerAmountLogMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(data)) {
            for (SysCustomerAmountLogEntity datum : data) {
                SysCustomerEntity entity = sysCustomerMapper.selectById(datum.getCustomerId());
                if (entity != null) {
                    datum.setCustomerName(entity.getCustomerName());
                    datum.setPhone(entity.getPhone());
                }
            }
        }
        return PageResult.of(data);
    }
}
