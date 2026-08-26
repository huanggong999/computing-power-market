package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysExtend;
import com.lingyang.cloud.entity.SysExtendWithdrawalRecord;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysExtendWithdrawalRecordMapper;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysExtendWithQuery;
import com.lingyang.cloud.service.SysExtendWithdrawalRecordService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysExtendWithdrawalRecordImpl implements SysExtendWithdrawalRecordService {

    @Autowired
    private SysExtendWithdrawalRecordMapper sysExtendWithdrawalRecordMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;


    @Override
    public Result<PageResult<SysExtendWithdrawalRecord>> getPage(PageQuery<SysExtendWithQuery> pageQuery) {
        SysExtendWithQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysExtendWithdrawalRecord> queryWrapper = Wrappers.lambdaQuery(SysExtendWithdrawalRecord.class)
                .eq(query.getUserId() != null, SysExtendWithdrawalRecord::getUserId, query.getUserId())
                .eq(query.getStatus() != null, SysExtendWithdrawalRecord::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysExtendWithdrawalRecord::getBankUserName, query.getName())
                .ge(StringUtils.isNotBlank(query.getStartTime()), SysExtendWithdrawalRecord::getCreateTime, query.getStartTime())
                .le(StringUtils.isNotBlank(query.getEndTime()), SysExtendWithdrawalRecord::getCreateTime, query.getEndTime())
                .orderByDesc(SysExtendWithdrawalRecord::getCreateTime);
        List<SysExtendWithdrawalRecord> value = sysExtendWithdrawalRecordMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysExtendWithdrawalRecord extend : value) {
                SysCustomerEntity customerEntity = sysCustomerMapper.selectById(extend.getUserId());
                if (customerEntity != null) {
                    extend.setUserName(customerEntity.getCustomerName());
                    extend.setUserPhone(customerEntity.getPhone());
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysExtendWithdrawalRecord> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }


}
