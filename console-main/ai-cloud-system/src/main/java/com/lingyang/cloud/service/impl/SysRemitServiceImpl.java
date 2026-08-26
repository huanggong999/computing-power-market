package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysNetworkValueEntity;
import com.lingyang.cloud.entity.SysRemit;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysRemitMapper;
import com.lingyang.cloud.model.dto.SysRemitQuery;
import com.lingyang.cloud.model.query.home.SysNetworkValueQuery;
import com.lingyang.cloud.service.SysRemitService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysRemitServiceImpl implements SysRemitService {

    @Autowired
    private SysRemitMapper sysRemitMapper;
    @Autowired
    private SysCustomerMapper sysCustomerMapper;


    @Override
    public Result<PageResult<SysRemit>> getPage(PageQuery<SysRemitQuery> pageQuery) {
        SysRemitQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysRemit> queryWrapper = Wrappers.lambdaQuery(SysRemit.class)
                .eq(query.getUserId() != null, SysRemit::getUserId, query.getUserId())
                .eq(query.getStatus() != null, SysRemit::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getRemitName()), SysRemit::getRemitName, query.getRemitName())
                .orderByDesc(SysRemit::getCreateTime);
        List<SysRemit> value = sysRemitMapper.selectList(queryWrapper);
        if (!CollectionUtils.isEmpty(value)) {
            for (SysRemit sysRemit : value) {
                SysCustomerEntity customer = sysCustomerMapper.selectById(sysRemit.getUserId());
                sysRemit.setCustomerName(customer.getCustomerName());
                sysRemit.setPhone(customer.getPhone());
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysRemit> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }
}
