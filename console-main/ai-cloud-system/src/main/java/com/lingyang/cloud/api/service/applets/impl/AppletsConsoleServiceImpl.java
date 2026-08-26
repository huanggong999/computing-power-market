package com.lingyang.cloud.api.service.applets.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.vo.PcConsoleSourceInfoVO;
import com.lingyang.cloud.api.service.applets.AppletsConsoleService;
import com.lingyang.cloud.api.service.pc.PcConsoleService;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.AppletsConsoleListDTO;
import com.lingyang.cloud.model.dto.AppletsIncomeDTO;
import com.lingyang.cloud.model.dto.AppletsIncomeListDTO;
import com.lingyang.cloud.model.vo.applets.AppletsIncomeListVO;
import com.lingyang.cloud.model.vo.applets.AppletsLoginBindVO;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.security.exception.LoginException;
import com.lingyang.common.security.utils.PasswordUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static com.lingyang.common.core.enums.HttpStatus.WECHAT_UNREGISTERED;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/3/7 10:42
 */
@Service
@Slf4j
public class AppletsConsoleServiceImpl implements AppletsConsoleService {

    @Resource
    private PcConsoleService pcConsoleService;

    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;

    @Resource
    private SysImageRepositoryMapper sysImageRepositoryMapper;

    @Resource
    private SysCustomerBucketMapper sysCustomerBucketMapper;

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private SysNetworkValueMapper sysNetworkValueMapper;

    @Resource
    private SysCustomerMapper sysCustomerMapper;

    @Resource
    private SysCustomerBillMapper sysCustomerBillMapper;

    @Resource
    private SysCustomerEcsWorkMapper sysCustomerEcsWorkMapper;
    @Override
    public AppletsConsoleListDTO list() {
        Long userId = SecurityContext.getUserInfo().getUserId();
        AppletsConsoleListDTO appletsConsoleListDTO = new AppletsConsoleListDTO();
        // 获取云服务器源信息
        PcConsoleSourceInfoVO sourceInfo = pcConsoleService.getSourceInfo(userId);
        BeanUtil.copyProperties(sourceInfo, appletsConsoleListDTO);
        appletsConsoleListDTO.setInstanceCount(appletsConsoleListDTO.getEcsRunningNumber() + sourceInfo.getEcsStoppedNumber() + sourceInfo.getExpiringNumber());
        // 获取容器信息
        List<SysCustomerContainerEntity> list = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getCustomerId, userId));
        if (ObjectUtils.isNotEmpty(list)){
            int normalNumber = list.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.RUNNING)).toList().size();
            int containerAbnormalNumber = list.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.ERROR)).toList().size();
            int containerTotal = list.size();
            appletsConsoleListDTO.setContainerTotal(containerTotal);
            appletsConsoleListDTO.setContainerNormalNumber(normalNumber);
            appletsConsoleListDTO.setContainerAbnormalNumber(containerAbnormalNumber);
            appletsConsoleListDTO.setContainerOtherNumber(containerTotal - normalNumber - containerAbnormalNumber);
        }
        // 获取镜像信息
        List<SysCustomerImageRepositoryEntity> customerImageRepositoryEntities = sysImageRepositoryMapper.selectList(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getCustomerId, userId));
        if (ObjectUtils.isNotEmpty(customerImageRepositoryEntities)){
            int imageRunningNumber = customerImageRepositoryEntities.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.RUNNING)).toList().size();
            int imageStoppedNumber = customerImageRepositoryEntities.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.STOPPED)).toList().size();
            int imageErrorNumber = customerImageRepositoryEntities.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.ERROR)).toList().size();
            int imageTotal = customerImageRepositoryEntities.size();
            appletsConsoleListDTO.setImageCount(imageTotal);
            appletsConsoleListDTO.setImageRunningNumber(imageRunningNumber);
            appletsConsoleListDTO.setImageStoppedNumber(imageStoppedNumber);
            appletsConsoleListDTO.setImageErrorNumber(imageErrorNumber);
        }
        // 获取存储对象信息
        List<SysCustomerBucketEntity> sysCustomerBucketEntities = sysCustomerBucketMapper.selectList(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                .eq(SysCustomerBucketEntity::getCustomerId, userId));
        if (ObjectUtils.isNotEmpty(sysCustomerBucketEntities)){
            appletsConsoleListDTO.setBucketCount(sysCustomerBucketEntities.size());
            int bucketObjectCount = 0;
            BigDecimal bucketObjectSize = new BigDecimal(0);
            for (SysCustomerBucketEntity sysCustomerBucketEntity : sysCustomerBucketEntities) {
                List<SysFile> fileList = sysFileMapper.selectList(Wrappers.lambdaQuery(SysFile.class)
                        .eq(SysFile::getBucketId, sysCustomerBucketEntity.getId())
                        .eq(SysFile::getType,2)
                        .eq(SysFile::getDelFlag, 0));
                BigDecimal totalSize = fileList.stream()
                        .map(SysFile::getSize)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                // 将MB转换为GB，并保留4位小数
                totalSize = totalSize.divide(new BigDecimal(1024), 4, RoundingMode.HALF_UP);
                bucketObjectCount += fileList.size();
                bucketObjectSize = bucketObjectSize.add(totalSize);
            }
            appletsConsoleListDTO.setBucketObjectCount(bucketObjectCount);
            appletsConsoleListDTO.setBucketObjectSize(bucketObjectSize);
        }
        //获取网络产品信息
        List<SysNetworkValueEntity> sysNetworkValueEntities = sysNetworkValueMapper.selectList(Wrappers.lambdaQuery(SysNetworkValueEntity.class)
                .eq(SysNetworkValueEntity::getUserId, userId)
                .eq(SysNetworkValueEntity::getDelFlag, 0)
                .eq(SysNetworkValueEntity::getFormType, 2)
                .eq(SysNetworkValueEntity::getPayStatus, 1));
        if (ObjectUtils.isNotEmpty(sysNetworkValueEntities)){
            int networkProductRunningNumber = sysNetworkValueEntities.stream().filter(i -> i.getActualStatus().equals(2)).toList().size();
            int networkProductStoppedNumber = sysNetworkValueEntities.stream().filter(i -> i.getActualStatus().equals(3)).toList().size();
            appletsConsoleListDTO.setNetworkProductCount(sysNetworkValueEntities.size());
            appletsConsoleListDTO.setNetworkProductRunningNumber(networkProductRunningNumber);
            appletsConsoleListDTO.setNetworkProductExpireNumber(networkProductStoppedNumber);
        }
        return appletsConsoleListDTO;
    }

    @Override
    public boolean bindPhone(AppletsLoginBindVO vo) {
        if (vo.getType()==1){
            SysCustomerEntity sysCustomerEntity = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getOpenId, vo.getOpenId()));
            if (ObjectUtils.isNotEmpty(sysCustomerEntity)){
                throw new LoginException(WECHAT_UNREGISTERED);
            }
            sysCustomerEntity.setOpenId(vo.getOpenId());
            sysCustomerMapper.updateById(sysCustomerEntity);
        }else if (vo.getType()==2){
            SysCustomerEntity customer = sysCustomerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEntity.class)
                    .eq(SysCustomerEntity::getPhone, vo.getPhone()));
            if (customer == null || !PasswordUtils.matchesPassword(vo.getPassword(), customer.getPassword())) {
                throw new LoginException("用户名或密码错误");
            }
            customer.setOpenId(vo.getOpenId());
            sysCustomerMapper.updateById(customer);
        }
        return true;
    }


    @Override
    public AppletsIncomeDTO incomeList(AppletsIncomeListVO vo) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        vo.setCustomerId(userId);
        AppletsIncomeDTO appletsIncomeDTO = new AppletsIncomeDTO();
        List<AppletsIncomeListDTO> list = sysCustomerBillMapper.incomeList(vo);
        if (ObjectUtils.isNotEmpty(list)){
            list.forEach(i -> {
                SysTransactionType transactionType = i.getTransactionType();
                i.setBillName(transactionType.getDesc());
            });
        }
        BigDecimal incomeTotal = list.stream().filter(i -> i.getTransactionType().equals(SysTransactionType.RECHARGE) || i.getTransactionType().equals(SysTransactionType.PLATFORM_GRANT) || i.getTransactionType().equals(SysTransactionType.REFUND)).map(AppletsIncomeListDTO::getChangeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outTotal = list.stream().filter(i -> i.getTransactionType().equals(SysTransactionType.PAY_DISCOUNT)).map(AppletsIncomeListDTO::getChangeAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        appletsIncomeDTO.setBillList(list);
        appletsIncomeDTO.setIncomeTotal(incomeTotal);
        appletsIncomeDTO.setOutTotal(outTotal);
        return appletsIncomeDTO;
    }
}
