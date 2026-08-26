package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerEntity;
import com.lingyang.cloud.entity.SysExtend;
import com.lingyang.cloud.entity.SysOrderEntity;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysExtendMapper;
import com.lingyang.cloud.mapper.SysOrderMapper;
import com.lingyang.cloud.model.query.home.SysExtendQuery;
import com.lingyang.cloud.model.query.home.SysFirstExtendQuery;
import com.lingyang.cloud.service.SysExtendService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.utils.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SysExtendServiceImpl implements SysExtendService {


    @Autowired
    private SysExtendMapper sysExtendMapper;

    @Autowired
    private SysCustomerMapper sysCustomerMapper;

    @Autowired
    private SysOrderMapper sysOrderMapper;

    @Override
    public Result<PageResult<SysExtend>> getPage(PageQuery<SysExtendQuery> pageQuery) {
        SysExtendQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysExtend> queryWrapper = Wrappers.lambdaQuery(SysExtend.class)
                .eq( SysExtend::getType, 1)
                .eq(query.getStatus() != null, SysExtend::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysExtend::getName, query.getName())
                .like(StringUtils.isNotBlank(query.getPhone()), SysExtend::getPhone, query.getPhone())
                .ge(StringUtils.isNotBlank(query.getStartTime()), SysExtend::getCreateTime, query.getStartTime())
                .le(StringUtils.isNotBlank(query.getEndTime()), SysExtend::getCreateTime, query.getEndTime())
                .orderByDesc(SysExtend::getCreateTime);
        List<SysExtend> value = sysExtendMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysExtend extend : value) {
                SysCustomerEntity customerEntity = sysCustomerMapper.selectById(extend.getUserId());
                if (customerEntity != null) {
                    extend.setUserName(customerEntity.getCustomerName());
                    extend.setUserPhone(customerEntity.getPhone());
                }
                if (extend.getParentUserId() != null && extend.getParentUserId() != 0) {
                    SysCustomerEntity p = sysCustomerMapper.selectById(extend.getParentUserId());
                    if (p != null) {
                        extend.setParentUserName(p.getCustomerName());
                        extend.setParentUserPhone(p.getPhone());
                    }

                    SysExtend sysExtend = sysExtendMapper.selectOne(
                            new LambdaQueryWrapper<SysExtend>()
                                    .eq(SysExtend::getUserId, extend.getParentUserId())
                    );
                    if (sysExtend != null) {
                        extend.setParentName(sysExtend.getName());
                        extend.setParentPhone(sysExtend.getPhone());
                    }
                }

                Long aLong = sysExtendMapper.selectCount(Wrappers.lambdaQuery(SysExtend.class)
                        .eq(SysExtend::getParentUserId, extend.getUserId()));
                extend.setFirstCount(Math.toIntExact(aLong));

                // 添加二级推广数量查询
                List<SysExtend> secondLevelExtends = sysExtendMapper.selectList(Wrappers.lambdaQuery(SysExtend.class)
                        .eq(SysExtend::getParentUserId, extend.getUserId()));

                if (CollectionUtils.isNotEmpty(secondLevelExtends)) {
                    List<Long> secondLevelUserIds = secondLevelExtends.stream()
                            .map(SysExtend::getUserId)
                            .collect(Collectors.toList());

                    Long secondCount = sysExtendMapper.selectCount(Wrappers.lambdaQuery(SysExtend.class)
                            .in(SysExtend::getParentUserId, secondLevelUserIds));

                    extend.setTwoCount(Math.toIntExact(secondCount));
                } else {
                    extend.setTwoCount(0);
                }
            }
        }
        return Result.success(Optional.of(value)
                .flatMap(PageResult::of)
                .orElseGet(() -> PageResult.of(List.of())));
    }

    @Override
    public Result<PageResult<SysExtend>> getFirstPage(PageQuery<SysFirstExtendQuery> pageQuery) {
        SysFirstExtendQuery query = pageQuery.getQuery();
        List<SysExtend> value2 = null;
        LambdaQueryWrapper<SysExtend> q1 = Wrappers.lambdaQuery(SysExtend.class)
                .eq(SysExtend::getParentUserId, query.getUserId())
                .orderByDesc(SysExtend::getCreateTime);
        List<SysExtend> value1 = sysExtendMapper.selectList(q1);
        if (CollectionUtils.isNotEmpty(value1)) {
            LambdaQueryWrapper<SysExtend> q2 = Wrappers.lambdaQuery(SysExtend.class)
                    .in(SysExtend::getParentUserId, value1.stream().map(SysExtend::getUserId).collect(Collectors.toList()))
                    .orderByDesc(SysExtend::getCreateTime);
            value2  = sysExtendMapper.selectList(q2);

        }



        pageQuery.startPage();
        if (query.getType().equals(1)) {
            log.info("一级推广");
            LambdaQueryWrapper<SysExtend> queryWrapper = Wrappers.lambdaQuery(SysExtend.class)
                    .eq(SysExtend::getParentUserId, query.getUserId())
                    .eq(query.getLevel() != null, SysExtend::getLevel, query.getLevel())
                    .ge(StringUtils.isNotBlank(query.getStartTime()), SysExtend::getCreateTime, query.getStartTime())
                    .le(StringUtils.isNotBlank(query.getEndTime()), SysExtend::getCreateTime, query.getEndTime())
                    .orderByDesc(SysExtend::getCreateTime);
            List<SysExtend> value = sysExtendMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(value)) {
                for (SysExtend extend : value) {
                    SysCustomerEntity customerEntity = sysCustomerMapper.selectById(extend.getUserId());
                    if (customerEntity != null) {
                        extend.setAvatar(customerEntity.getAvatar());
                        extend.setUserName(customerEntity.getCustomerName());
                        extend.setUserPhone(customerEntity.getPhone());
                    }

                    SysOrderEntity order = sysOrderMapper.selectOne(
                            new QueryWrapper<SysOrderEntity>()
                                    .select(" count(*) as firstUserId , sum(first_user_commission) as firstUserCommission ")
                                    .eq("create_by_id", extend.getUserId())
                                    .eq("first_user_id", query.getUserId())
                                    .eq("order_status", OrderStatusEnum.PAID)
                    );

                    if (order != null) {
                        extend.setSgOrder(order.getFirstUserId());
                        extend.setSgAmount(order.getFirstUserCommission());
                    }

                }
            }
            return Result.success(Optional.of(value)
                    .flatMap(entities -> {
                        PageResult<SysExtend> result = PageResult.of(entities);
                        return result;
                    })
                    .orElseGet(() -> {
                        return PageResult.of(List.of());
                    }));
        } else {
            log.info("r 级推广");
            if (CollectionUtils.isNotEmpty(value2)) {
                log.info("r 222222级推广");
                List<Long> coll1 = value2.stream().map(SysExtend::getUserId).collect(Collectors.toList());
                LambdaQueryWrapper<SysExtend> queryWrapper = Wrappers.lambdaQuery(SysExtend.class)
                        .in(SysExtend::getUserId, coll1)
                        .orderByDesc(SysExtend::getCreateTime);
                List<SysExtend> value = sysExtendMapper.selectList(queryWrapper);
                if (CollectionUtils.isNotEmpty(value)) {
                    for (SysExtend extend : value) {
                        SysCustomerEntity customerEntity = sysCustomerMapper.selectById(extend.getUserId());
                        if (customerEntity != null) {
                            extend.setAvatar(customerEntity.getAvatar());
                            extend.setUserName(customerEntity.getCustomerName());
                            extend.setUserPhone(customerEntity.getPhone());
                        }

                        SysOrderEntity order = sysOrderMapper.selectOne(
                                new QueryWrapper<SysOrderEntity>()
                                        .select(" count(*) as firstUserId , sum(first_user_commission) as firstUserCommission ")
                                        .eq("create_by_id", extend.getUserId())
                                        .in("first_user_id", coll1)
                                        .eq("two_user_id", query.getUserId())
                                        .eq("order_status", OrderStatusEnum.PAID)
                        );

                        if (order != null) {
                            extend.setSgOrder(order.getFirstUserId());
                            extend.setSgAmount(order.getFirstUserCommission());
                        }

                    }
                }
                return Result.success(Optional.of(value)
                        .flatMap(entities -> {
                            PageResult<SysExtend> result = PageResult.of(entities);
                            return result;
                        })
                        .orElseGet(() -> {
                            return PageResult.of(List.of());
                        }));
            } else {
                return Result.success(PageResult.of(new ArrayList<>()));
            }

        }

    }




}
