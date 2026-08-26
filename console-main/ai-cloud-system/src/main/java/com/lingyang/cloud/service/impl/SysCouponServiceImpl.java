package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCouponEntity;
import com.lingyang.cloud.mapper.SysCouponMapper;
import com.lingyang.cloud.model.query.coupon.SysCouponQuery;
import com.lingyang.cloud.service.SysCouponService;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.StringUtils;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/23 15:03
 */
@Service
public class SysCouponServiceImpl implements SysCouponService {

    @Resource
    private SysCouponMapper sysCouponMapper;

    @Override
    public List<SysCouponEntity> getList(SysCouponQuery query) {
        LambdaQueryWrapper<SysCouponEntity> queryWrapper = Wrappers.lambdaQuery(SysCouponEntity.class)
                .like(StringUtils.isNotEmpty(query.getName()), SysCouponEntity::getName, query.getName())
                .eq(ObjectUtils.isNotEmpty(query.getType()), SysCouponEntity::getType, query.getType())
                .eq(ObjectUtils.isNotEmpty(query.getStatus()), SysCouponEntity::getStatus, query.getStatus())
                .le(query.getUseStartTime() != null, SysCouponEntity::getUseTimeStart, query.getUseStartTime())
                .ge(query.getUseEndTime() != null, SysCouponEntity::getUseTimeEnd, query.getUseEndTime())
                .le(query.getReceiveTimeStart() != null, SysCouponEntity::getReceiveTimeStart, query.getReceiveTimeStart())
                .ge(query.getReceiveTimeEnd() != null, SysCouponEntity::getReceiveTimeEnd, query.getReceiveTimeEnd())
                .in(ObjectUtils.isNotEmpty(query.getCouponIdList()), SysCouponEntity::getId, query.getCouponIdList())
                .orderByAsc(SysCouponEntity::getSort)
                .orderByDesc(SysCouponEntity::getUpdateTime);
        return sysCouponMapper.selectList(queryWrapper);
    }

    @Override
    public SysCouponEntity getDetail(Long id) {
        return sysCouponMapper.selectById(id);
    }

    @Override
    public Boolean save(SysCouponEntity dto) {
        return sysCouponMapper.insert(dto) > 0;
    }

    @Override
    public void checkSaveParams(SysCouponEntity d) throws HttpParamsException {
        Long count = sysCouponMapper.selectCount(Wrappers.lambdaQuery(SysCouponEntity.class)
                .eq(SysCouponEntity::getName, d.getName()));
        if (count != null && count > 0) {
            throw new HttpParamsException("优惠卷名称已存在");
        }
    }

    @Override
    public Boolean update(SysCouponEntity dto) {
        return sysCouponMapper.updateById(dto) > 0;
    }

    @Override
    public void checkUpdateParams(SysCouponEntity d) throws HttpParamsException {
        Optional.of(d.getId())
                .flatMap(id -> {
                    Optional.of(d.getName())
                            .ifPresent(name -> {
                                SysCouponEntity entity = sysCouponMapper.selectOne(Wrappers.lambdaQuery(SysCouponEntity.class)
                                        .eq(SysCouponEntity::getName, name));
                                if (ObjectUtils.isNotEmpty(entity) && !entity.getId().equals(id)) {
                                    throw new HttpParamsException("优惠卷名称已存在");
                                }
                            });
                    return id;
                })
                .orElseThrow(() -> new HttpParamsException("优惠卷不存在"));
    }

    @Override
    public Boolean remove(Long id) {
        // todo 删除客户相关的优惠卷
        return sysCouponMapper.deleteById(id) > 0;
    }
}