package com.lingyang.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcVoucherListDTO;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.SysActiveCouponDTO;
import com.lingyang.cloud.model.dto.SysActiveCouponDetailsDTO;
import com.lingyang.cloud.model.dto.SysActiveDetailsDTO;
import com.lingyang.cloud.model.query.active.SysActiveCouponQuery;
import com.lingyang.cloud.model.query.active.SysActiveQuery;
import com.lingyang.cloud.model.query.active.SysActiveRecordQuery;
import com.lingyang.cloud.model.vo.active.SysActiveVO;
import com.lingyang.cloud.service.SysActiveCenterService;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/1/16 15:23
 */
@Service
@EnableScheduling
public class SysActiveCenterServiceImpl implements SysActiveCenterService {

    @Autowired
    private SysActiveCenterMapper sysActiveCenterMapper;

    @Autowired
    private SysCouponMapper sysCouponMapper;

    @Autowired
    private SysActiveCouponMapper sysActiveCouponMapper;

    @Autowired
    private SysActiveRecordMapper sysActiveRecordMapper;

    @Autowired
    private SysRechargeActivityMapper sysRechargeActivityMapper;

    @Autowired
    private SysRechargeActivityRewardsMapper sysRechargeActivityRewardsMapper;


    @Value(
            "${extend.shareUrl}"
    )
    private String extendShare;

    @Override
    public Result<PageResult<SysActiveCenterEntity>> getPage(PageQuery<SysActiveQuery> pageQuery) {
        pageQuery.startPage();
        List<SysActiveCenterEntity> entities = sysActiveCenterMapper.getPage(pageQuery.getQuery());
        PageResult<SysActiveCenterEntity> result = PageResult.of(entities);
        return Result.success(result);
    }

    @Override
    public Result<PageResult<SysActiveCouponDTO>> getCoupons(PageQuery<SysActiveCouponQuery> pageQuery) {
        pageQuery.startPage();
        List<SysActiveCouponDTO> entities =sysCouponMapper.getCoupons(pageQuery.getQuery());
        PageResult<SysActiveCouponDTO> result = PageResult.of(entities);
        return Result.success(result);
    }

    @Override
    public Result<Void> save(SysActiveVO vo) {
        SysActiveCenterEntity sysActiveCenterEntity = new SysActiveCenterEntity();
        BeanUtils.copyProperties(vo,sysActiveCenterEntity);
        sysActiveCenterMapper.insert(sysActiveCenterEntity);
        extracted(vo, sysActiveCenterEntity);
        return Result.success();
    }

    @Override
    public Result<SysActiveDetailsDTO> getActiveDetails(Long id) {
        SysActiveDetailsDTO dto = new SysActiveDetailsDTO();
        SysActiveCenterEntity sysActiveCenterEntity = sysActiveCenterMapper.selectOne(Wrappers.lambdaQuery(SysActiveCenterEntity.class).eq(SysActiveCenterEntity::getId, id));
        BeanUtils.copyProperties(sysActiveCenterEntity, dto);

        // 查询优惠劵列表
        List<SysActiveCouponDetailsDTO> coupons = sysActiveCouponMapper.getCoupons(id);

        // 过滤和转换优惠券列表
        List<SysActiveCouponDTO> list1 = filterAndConvertCoupons(coupons, 1);
        List<SysActiveCouponDTO> list2 = filterAndConvertCoupons(coupons, 2);

        dto.setList(list1);
        dto.setList2(list2);
        return Result.success(dto);
        }

    private List<SysActiveCouponDTO> filterAndConvertCoupons(List<SysActiveCouponDetailsDTO> coupons, int couponType) {
        return coupons.stream()
                .filter(coupon -> coupon.getCouponType() == couponType)
                .map(coupon -> BeanUtil.copyProperties(coupon, SysActiveCouponDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public Result<Void> update(SysActiveVO vo) {
        //先删除已有优惠卷
        sysActiveCouponMapper.delete(Wrappers.lambdaQuery(SysActiveCouponEntity.class).eq(SysActiveCouponEntity::getActiveId,vo.getId()));
        SysActiveCenterEntity sysActiveCenterEntity = BeanUtil.copyProperties(vo, SysActiveCenterEntity.class);
        sysActiveCenterMapper.updateById(sysActiveCenterEntity);
        extracted(vo, sysActiveCenterEntity);
        return Result.success();
    }

    private void extracted(SysActiveVO vo, SysActiveCenterEntity sysActiveCenterEntity) {
        List<SysActiveCouponEntity> list = new ArrayList<>();
        if (ObjectUtils.isNotEmpty(vo.getCouponIds())){
            vo.getCouponIds().forEach(couponId -> {
                SysActiveCouponEntity sysActiveCoupon = new SysActiveCouponEntity();
                sysActiveCoupon.setActiveId(sysActiveCenterEntity.getId());
                sysActiveCoupon.setCouponId(couponId);
                sysActiveCoupon.setType(1);
                list.add(sysActiveCoupon);
            });
        }
        if (ObjectUtils.isNotEmpty(vo.getCouponIds2())){
            vo.getCouponIds2().forEach(couponId -> {
                SysActiveCouponEntity sysActiveCoupon = new SysActiveCouponEntity();
                sysActiveCoupon.setActiveId(sysActiveCenterEntity.getId());
                sysActiveCoupon.setCouponId(couponId);
                sysActiveCoupon.setType(2);
                list.add(sysActiveCoupon);
            });
        }
        sysActiveCouponMapper.batchInsert(list);
    }

    @Override
    public Result<SysActiveDetailsDTO> getPcActiveDetails(Long id) {
        Long userId = null;
        try {
            userId = SecurityContext.getUserInfo().getUserId();
        }catch (Exception e){
        }
        SysActiveCenterEntity sysActiveCenterEntity = sysActiveCenterMapper.selectOne(Wrappers.lambdaQuery(SysActiveCenterEntity.class).eq(SysActiveCenterEntity::getId, id));
        SysActiveDetailsDTO dto = BeanUtil.copyProperties(sysActiveCenterEntity, SysActiveDetailsDTO.class);
        if (ObjectUtils.isEmpty(userId)){
            dto.setLink("");
        }else {
            String url = extendShare + "activity?active_id=" + id + "&user_id=" + userId;
            dto.setLink(url);
        }
        List<SysActiveCenterEntity> list = sysActiveCenterMapper.selectList(Wrappers.lambdaQuery(SysActiveCenterEntity.class)
                .eq(SysActiveCenterEntity::getStatus, 2)
                .ne(SysActiveCenterEntity::getId, id)
                .last("LIMIT 4"));
        dto.setRecommendedList(list);
        return Result.success(dto);
    }

    @Override
    public Result<PageResult<SysActiveRecordEntity>> getActiveRecord(PageQuery<SysActiveRecordQuery> pageQuery) {
        SysActiveRecordQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        List<SysActiveRecordEntity> value = sysActiveRecordMapper.getActiveRecord(query);
        PageResult<SysActiveRecordEntity> result = PageResult.of(value);
        return Result.success(result);
    }

    @Override
    public Result<List<SysActiveDetailsDTO>> getActiveList() {
        Long userId = null;
        try {
            userId = SecurityContext.getUserInfo().getUserId();
        } catch (Exception ignored) {
        }
        List<SysActiveCenterEntity> list = sysActiveCenterMapper.selectList(Wrappers.lambdaQuery(SysActiveCenterEntity.class)
                .eq(SysActiveCenterEntity::getStatus, 2));
        List<SysActiveDetailsDTO> sysActiveDetailsDTOS = BeanUtil.copyToList(list, SysActiveDetailsDTO.class);
        if (ObjectUtils.isNotEmpty(sysActiveDetailsDTOS)){
            for (SysActiveDetailsDTO sysActiveDetailsDTO : sysActiveDetailsDTOS) {
                if (ObjectUtils.isEmpty(userId)){
                    sysActiveDetailsDTO.setLink("");
                }else {
                    String url = extendShare + "activity?active_id=" + sysActiveDetailsDTO.getId() + "&user_id=" + userId;
                    sysActiveDetailsDTO.setLink(url);
                }
            }
        }
        return Result.success(sysActiveDetailsDTOS);
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void updateCouponStatus() {
        List<SysActiveCenterEntity> list = sysActiveCenterMapper.selectList(Wrappers.lambdaQuery(SysActiveCenterEntity.class));
        if (ObjectUtils.isNotEmpty(list)) {
            list.forEach(sysActiveCenterEntity -> {
                int newStatus = sysActiveCenterEntity.getStatus();
                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                Date nextDate = calendar.getTime();

                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(currentDate);
                calendar2.add(Calendar.DAY_OF_MONTH, 1);
                Date nextDate2 = calendar2.getTime();

                if (sysActiveCenterEntity.getEndDate().before(nextDate)) {
                    newStatus = 3;
                } else if (sysActiveCenterEntity.getStartDate().before(nextDate2) && sysActiveCenterEntity.getEndDate().after(nextDate)) {
                    newStatus = 2;
                }
                if (newStatus != sysActiveCenterEntity.getStatus()) {
                    sysActiveCenterEntity.setStatus(newStatus);
                    sysActiveCenterMapper.updateById(sysActiveCenterEntity);
                }
            });
        }
    }

    @Override
    public Result<List<PcVoucherListDTO>> getCouponList() {
        List<SysRechargeActivity> sysRechargeActivities = sysRechargeActivityMapper.selectList(Wrappers.lambdaQuery(SysRechargeActivity.class)
                .eq(SysRechargeActivity::getStatus, 1)
                .le(SysRechargeActivity::getRechargeStartTime, new Date())
                .ge(SysRechargeActivity::getRechargeEndTime, new Date()));
        List<PcVoucherListDTO> list = new ArrayList<>();
        if (ObjectUtils.isEmpty(sysRechargeActivities)){
            return Result.success(list);
        }
        for (SysRechargeActivity sysRechargeActivity : sysRechargeActivities) {
            List<SysRechargeActivityRewards> sysRechargeActivityRewards = sysRechargeActivityRewardsMapper.selectList(Wrappers.lambdaQuery(SysRechargeActivityRewards.class)
                    .eq(SysRechargeActivityRewards::getActivityId, sysRechargeActivity.getId()));
            for (SysRechargeActivityRewards sysRechargeActivityReward : sysRechargeActivityRewards) {
                PcVoucherListDTO dto = new PcVoucherListDTO();
                dto.setCouponEndTime(sysRechargeActivity.getCouponEndTime());
                dto.setCouponStartTime(sysRechargeActivity.getCouponStartTime());
                dto.setCouponAmount(sysRechargeActivityReward.getCouponAmount());
                dto.setRechargeAmount(sysRechargeActivityReward.getRechargeAmount());
                list.add(dto);
            }
        }
        return Result.success(list);
    }

    @Override
    public Result<Void> delete(Long id) {
        sysActiveCenterMapper.deleteById(id);
        return Result.success();
    }
}
