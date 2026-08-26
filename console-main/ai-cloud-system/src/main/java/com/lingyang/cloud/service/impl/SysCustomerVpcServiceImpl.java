package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.SysCustomerVpcEntity;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysCustomerVpcMapper;
import com.lingyang.cloud.service.SysCustomerVpcService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.volcengine.ApiException;
import com.volcengine.vpc.VpcApi;
import com.volcengine.vpc.model.CreateVpcRequest;
import com.volcengine.vpc.model.DescribeVpcAttributesRequest;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/27 20:13
 */
@Service
public class SysCustomerVpcServiceImpl implements SysCustomerVpcService {
    @Resource
    private SysCustomerVpcMapper sysCustomerVpcMapper;
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private VolEngineConfig volEngineConfig;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public SysCustomerVpcEntity getCustomerVpcOnInsert(Long customerId, SourceRegionsEnum regions) throws ApiException {
        // 处理私有网络
        SysCustomerVpcEntity vpcEntity = sysCustomerVpcMapper.selectOne(Wrappers.lambdaQuery(SysCustomerVpcEntity.class)
                .eq(SysCustomerVpcEntity::getCustomerId, customerId)
                .eq(SysCustomerVpcEntity::getRegionId, regions));
        if (vpcEntity == null) {
            // 创建vpc
            String customerName = sysCustomerMapper.selectById(customerId).getCustomerName();
            CreateVpcRequest vpcRequest = new CreateVpcRequest();
            vpcRequest.setVpcName(customerName + "-" + regions.getId());
            vpcRequest.setCidrBlock("172.16.0.0/12");
            vpcRequest.setDescription(customerName + "-" + regions.getId());
            vpcRequest.setEnableIpv6(false);
            vpcRequest.setProjectName(volEngineConfig.getProjectName());
            VpcApi vpcApi = VoEngineApiClient.getVpcApi(regions.getId());
            String vpcId = vpcApi.createVpc(vpcRequest).getVpcId();
            vpcEntity = new SysCustomerVpcEntity();
            vpcEntity.setCidrBlock(vpcRequest.getCidrBlock());
            vpcEntity.setVpcId(vpcId);
            vpcEntity.setRegionId(regions);
            vpcEntity.setCustomerId(customerId);
            vpcEntity.setVpcName(vpcRequest.getVpcName());
            vpcEntity.setVpcDesc(vpcRequest.getDescription());
            vpcEntity.setStatus("Creating");
            String vpcStatus;
            if (!"Available".equals(vpcEntity.getStatus())) {
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                    DescribeVpcAttributesRequest describeVpcsRequest = new DescribeVpcAttributesRequest();
                    describeVpcsRequest.setVpcId(vpcEntity.getVpcId());
                    vpcStatus = vpcApi.describeVpcAttributes(describeVpcsRequest).getStatus();
                } while (!"Available".equals(vpcStatus));
                vpcEntity.setStatus(vpcStatus);
                sysCustomerVpcMapper.insert(vpcEntity);
            }
        }
        return vpcEntity;
    }
}
