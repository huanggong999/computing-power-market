package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.entity.SysNetworkProductEntity;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.SysCustomerDiscountMapper;
import com.lingyang.cloud.mapper.SysCustomerNetworkMapper;
import com.lingyang.cloud.mapper.SysNetworkFormMapper;
import com.lingyang.cloud.mapper.SysNetworkProductMapper;
import com.lingyang.cloud.model.dto.feilian.AddDepartmentDTO;
import com.lingyang.cloud.model.query.home.SysHomeEcsQuery;
import com.lingyang.cloud.model.vo.feilian.CreateDepartmentVO;
import com.lingyang.cloud.model.vo.feilian.UpdateDepartmentVO;
import com.lingyang.cloud.service.SysNetworkProductService;
import com.lingyang.cloud.utils.FeiLianUtils;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.Optional;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysNetworkProductServiceImpl implements SysNetworkProductService {

    @Autowired
    private SysNetworkProductMapper sysNetworkProductMapper;

    @Autowired
    private SysNetworkFormMapper sysNetworkFormMapper;

    @Autowired
    private SysCustomerNetworkMapper sysCustomerNetworkMapper;
    @Resource
    private SysCustomerDiscountMapper sysCustomerDiscountMapper;

    @Resource
    private FeiLianUtils flashLianUtils;

    @Value("${feiLian.parentDepartmentId}")
    private String parentDepartmentId;
    @Override
    public Result<PageResult<SysNetworkProductEntity>> getPage(PageQuery<SysHomeEcsQuery> pageQuery) {
        SysHomeEcsQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        LambdaQueryWrapper<SysNetworkProductEntity> queryWrapper = Wrappers.lambdaQuery(SysNetworkProductEntity.class)
                .eq(query.getStatus() != null, SysNetworkProductEntity::getStatus, query.getStatus())
                .like(StringUtils.isNotBlank(query.getName()), SysNetworkProductEntity::getName, query.getName())
                .orderByDesc(SysNetworkProductEntity::getCreateTime);
        List<SysNetworkProductEntity> value = sysNetworkProductMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(value)) {
            for (SysNetworkProductEntity sysNetworkProductEntity : value) {
                 SysNetworkFormEntity sysNetworkFormEntity = sysNetworkFormMapper.selectById(sysNetworkProductEntity.getFormId());
                 if (sysNetworkFormEntity != null) {
                     sysNetworkProductEntity.setFormName(sysNetworkFormEntity.getName());
                 }
                SysNetworkFormEntity payf = sysNetworkFormMapper.selectById(sysNetworkProductEntity.getPayFormId());
                if (payf != null) {
                    sysNetworkProductEntity.setPayFormName(payf.getName());
                }

                if (StringUtils.isNotBlank(SecurityContext.getTokenKey())) {
                    LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
                if (userInfo != null) {
                    // 处理折扣
                    List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                            new LambdaQueryWrapper<SysCustomerNetwork>()
                                    .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                                    .eq(SysCustomerNetwork::getNetworkId, sysNetworkProductEntity.getId())
                    );
                    BigDecimal orDefault = null;
                    if (CollectionUtils.isNotEmpty(networks)) {
                        SysCustomerNetwork e = networks.get(0);
                        orDefault = e.getDiscountRation();
                    } else {
                        // 处理平台溢价
                        Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                                        .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                                .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                                .orElse(new HashMap<>(0));
                        orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
                    }

                    if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
                        // sysNetworkProductEntity.setPayPrice(e.getDiscountRation().setScale(2, RoundingMode.UP));
                    } else {
                        sysNetworkProductEntity.setPayPrice(sysNetworkProductEntity.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));

                }
  }
                }




            }
        }
        return Result.success(Optional.of(value)
                .flatMap(entities -> {
                    PageResult<SysNetworkProductEntity> result = PageResult.of(entities);
                    return result;
                })
                .orElseGet(() -> {
                    return PageResult.of(List.of());
                }));
    }

    @Override
    public SysNetworkProductEntity getById(Long id) {

        SysNetworkProductEntity sysNetworkProductEntity = sysNetworkProductMapper.selectById(id);


        if (StringUtils.isNotBlank(SecurityContext.getTokenKey())) {
            // 处理折扣
            LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
        List<SysCustomerNetwork> networks = sysCustomerNetworkMapper.selectList(
                new LambdaQueryWrapper<SysCustomerNetwork>()
                        .eq(SysCustomerNetwork::getUserId, userInfo.getUserId())
                        .eq(SysCustomerNetwork::getNetworkId, sysNetworkProductEntity.getId())
        );
        BigDecimal orDefault = null;
        if (CollectionUtils.isNotEmpty(networks)) {
            SysCustomerNetwork e = networks.get(0);
            orDefault = e.getDiscountRation();
        } else {
            // 处理平台溢价
            Map<SourceTypeEnum, BigDecimal> sourceDiscount = Optional.of(sysCustomerDiscountMapper.selectList(Wrappers.lambdaQuery(SysCustomerDiscountEntity.class)
                            .eq(SysCustomerDiscountEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())))
                    .flatMap(list -> list.stream().collect(Collectors.toMap(SysCustomerDiscountEntity::getSourceType, SysCustomerDiscountEntity::getDiscountRation)))
                    .orElse(new HashMap<>(0));
            orDefault = sourceDiscount.get(SourceTypeEnum.AGIC);
        }

        if (orDefault == null || orDefault.compareTo(BigDecimal.ONE) == 0) {
            // sysNetworkProductEntity.setPayPrice(e.getDiscountRation().setScale(2, RoundingMode.UP));
        } else {
            sysNetworkProductEntity.setPayPrice(sysNetworkProductEntity.getPayPrice().multiply(orDefault).setScale(2, RoundingMode.UP));
        }

        }
        return sysNetworkProductEntity;
    }

    @Override
    public void save(SysNetworkProductEntity entity) {
        //添加飞连部门
        CreateDepartmentVO vo = new CreateDepartmentVO();
        vo.setType(2);
        vo.setParentId(parentDepartmentId);
        vo.setName(entity.getName());
        AddDepartmentDTO department = flashLianUtils.createDepartment(vo);
        log.info("创建部门成功:{}", department);
        String departmentId = department.getId();
        entity.setDepartmentId(departmentId);
        sysNetworkProductMapper.insert(entity);
    }

    @Override
    public void update(SysNetworkProductEntity entity) {
        // 查询数据库中的原始记录
        SysNetworkProductEntity originalEntity = sysNetworkProductMapper.selectById(entity.getId());

        // 检查传入的 name 是否与数据库中已有的 name 不同
        if (originalEntity != null && !originalEntity.getName().equals(entity.getName())) {
            //修改飞连部门名称
            UpdateDepartmentVO vo1 = new UpdateDepartmentVO();
            vo1.setId(originalEntity.getDepartmentId());
            vo1.setName(entity.getName());
            vo1.setParentId(parentDepartmentId);
            flashLianUtils.updateDepartment(vo1);
        }
        sysNetworkProductMapper.updateById(entity);
    }
}
