package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.service.SysCustomerVpcService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.*;
import com.volcengine.storageebs.StorageEbsApi;
import com.volcengine.storageebs.model.DescribeVolumesRequest;
import com.volcengine.storageebs.model.VolumeForDescribeVolumesOutput;
import com.volcengine.vpc.VpcApi;
import com.volcengine.vpc.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/11/20 19:03
 */
@Service
@Slf4j
public class CustomerInstanceCreateQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;
    @Resource
    private SysCustomerEipMapper sysCustomerEipMapper;
    @Resource
    private SysCustomerVolumeMapper sysCustomerVolumeMapper;
    @Resource
    private SysCustomerSubnetMapper sysCustomerSubnetMapper;
    @Resource
    private SysCustomerVpcService sysCustomerVpcService;
    @Resource
    private SysCustomerSecurityGroupMapper sysCustomerSecurityGroupMapper;
    @Resource
    private VolEngineConfig volEngineConfig;
    @Resource
    private SysOrderService sysOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        log.info("订单资源id：{}", messageContent);
        SysCustomerInstancesEntity instancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getOrderSourceUid, messageContent));
        log.info("查询订单: {} {}", messageContent, instancesEntity);
        if (instancesEntity == null || !instancesEntity.getStatus().equals(EcsStatusEnum.CREATING)) {
            return;
        }
        log.info("查询订单 查询地域: {}", messageContent);
        List<SysCustomerVolumeEntity> customerVolumeList = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                .eq(SysCustomerVolumeEntity::getOrderSourceUid, messageContent));
        log.info("查询订单 查询地域数据: {}", instancesEntity.getRegion().getId());
        EcsApi ecsApi = VoEngineApiClient.getEcsApi(instancesEntity.getRegion().getId());
        log.info("创建查询地域： {} {}", messageContent, JSON.toJSONString(ecsApi));
        RunInstancesRequest request = new RunInstancesRequest();
        try {
            // 创建服务器
            request.setUserData(new String(Base64.getEncoder().encode(messageContent.getBytes(StandardCharsets.UTF_8))));
            request.setAutoRenew(false);
            request.setCount(1);
            request.setDescription(instancesEntity.getDescription());
            request.setHostname(instancesEntity.getHostName());
            request.setImageId(instancesEntity.getImageId());
            request.setInstallRunCommandAgent(instancesEntity.getCommandAgent());
            request.setInstanceChargeType(instancesEntity.getChargeType().getVolcengineDesc());
            request.setInstanceName(ObjectUtils.defaultIfNull(instancesEntity.getInstanceName(), IdUtils.randomUUID()));
            request.setInstanceTypeId(instancesEntity.getEcsScale());
            request.setMinCount(1);
            request.setPassword(instancesEntity.getPassword());
            request.period(instancesEntity.getDuration());
            request.setPeriodUnit(instancesEntity.getDurationUnit().getVolcengineDesc());
            request.setSpotStrategy("NoSpot");
            request.setZoneId(instancesEntity.getZoneId());
            request.setClientToken(IdUtils.fastSimpleUUID());
            request.setProjectName(volEngineConfig.getProjectName());
            SysCustomerEipEntity eipEntity = sysCustomerEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEipEntity.class)
                    .eq(SysCustomerEipEntity::getOrderSourceUid, messageContent));
            EipAddressForRunInstancesInput eipInput = new EipAddressForRunInstancesInput();
            eipInput.setISP(eipEntity.getIsp());
            eipInput.setBandwidthMbps(eipEntity.getBandwidth());
            eipInput.setChargeType(eipEntity.getBillingType());
            eipInput.setReleaseWithInstance(true);
            request.setEipAddress(eipInput);
            request.setVolumes(getVolumeForRunInstancesInputs(customerVolumeList));
            request.setNetworkInterfaces(getNetworkInterfaces(instancesEntity));
            log.info("创建云服务器参数： {} {}", messageContent, JSON.toJSONString(request));
            String instanceId = ecsApi.runInstances(request).getInstanceIds().get(0);
            String status = "CREATING";
            InstanceForDescribeInstancesOutput instancesOutput = null;
            do {
                log.info("未创建成功，再次创建： {} {}", messageContent, instanceId);
                Thread.sleep(1000L);
                try {
                    // 实列信息
                    DescribeInstancesRequest ecsQueryRequest = new DescribeInstancesRequest();
                    ecsQueryRequest.setInstanceIds(List.of(instanceId));
                    ecsQueryRequest.setMaxResults(1);
                    DescribeInstancesResponse ecsResponse = ecsApi.describeInstances(ecsQueryRequest);
                    log.info("未创建成功，再次创建返回： {} {} ecsResponse:{}", messageContent, instanceId, JSON.toJSONString(ecsResponse));
                    List<InstanceForDescribeInstancesOutput> instances = ecsResponse.getInstances();
                    if (ObjectUtils.isNotEmpty(instances)){
                        instancesOutput = instances.get(0);
                        EipAddressForDescribeInstancesOutput eipAddress = instancesOutput.getEipAddress();
                        if (ObjectUtils.isNotEmpty(eipAddress)) {
                            eipEntity.setEipAddress(eipAddress.getIpAddress());
                            eipEntity.setEipId(eipAddress.getAllocationId());
                            eipEntity.setEipName(eipAddress.getAllocationId());
                        }
                        log.info("未创建成功，再次创建返回状态： {} {} status:{}", messageContent, instanceId, instancesOutput.getStatus());
                        status = instancesOutput.getStatus();
                    }

                } catch (ApiException ignored) {
                    log.info("创建服务器异常 ignored: {}", messageContent, ignored);
                }
            } while (!"RUNNING".equals(status));
            instancesEntity.setStatus(EcsStatusEnum.valueOf(status));
            instancesEntity.setInstanceId(instanceId);
            eipEntity.setInstanceId(instanceId);
            eipEntity.setCustomerInstanceId(instancesEntity.getId());
            // 获取云盘信息
            StorageEbsApi storageEbsApi = VoEngineApiClient.getStorageEbsApi(instancesEntity.getRegion().getId());
            DescribeVolumesRequest volumesRequest = new DescribeVolumesRequest();
            volumesRequest.setInstanceId(instanceId);
            volumesRequest.setPageSize(100);
            volumesRequest.setZoneId(instancesEntity.getZoneId());
            Map<String, List<VolumeForDescribeVolumesOutput>> kindVolumeMap = storageEbsApi.describeVolumes(volumesRequest).getVolumes().stream()
                    .collect(Collectors.groupingBy(VolumeForDescribeVolumesOutput::getKind));
            AtomicInteger volumeCount = new AtomicInteger(0);
            customerVolumeList.forEach(e -> {
                List<VolumeForDescribeVolumesOutput> volumesOutputs = kindVolumeMap.get(e.getKind());
                if ("system".equals(e.getKind())) {
                    e.setVolumeId(volumesOutputs.get(0).getVolumeId());
                } else {
                    e.setVolumeId(volumesOutputs.get(volumeCount.getAndIncrement()).getVolumeId());
                }
                e.setCustomerId(instancesEntity.getCustomerId());
                e.setInstanceId(instanceId);
                e.setCustomerInstanceId(instancesEntity.getId());
            });
            sysCustomerVolumeMapper.batchUpdateById(customerVolumeList);
            sysCustomerEipMapper.updateById(eipEntity);
            sysCustomerInstancesMapper.updateById(instancesEntity);
        } catch (Exception e) {
            log.info("创建服务器异常: {}", messageContent, e);
            // 删除创建的子网和安全组
            List<NetworkInterfaceForRunInstancesInput> networkInterfaces = request.getNetworkInterfaces();
            if (ObjectUtils.isNotEmpty(networkInterfaces)) {
                NetworkInterfaceForRunInstancesInput instancesInput = networkInterfaces.get(0);
                Optional.of(instancesInput.getSubnetId())
                        .ifPresent(subnetId -> {
                            VpcApi vpcApi = VoEngineApiClient.getVpcApi(instancesEntity.getRegion().getId());
                            DeleteSubnetRequest deleteSubnetRequest = new DeleteSubnetRequest();
                            deleteSubnetRequest.setSubnetId(subnetId);
                            try {
                                vpcApi.deleteSubnet(deleteSubnetRequest);
                            } catch (ApiException ignored) {
                                log.info("创建服务器异常 1ignored: {}", messageContent, ignored);
                            }
                        });
                Optional.of(instancesInput.getSecurityGroupIds())
                        .ifPresent(securityGroupIds -> {
                            VpcApi vpcApi = VoEngineApiClient.getVpcApi(instancesEntity.getRegion().getId());
                            for (String securityGroupId : securityGroupIds) {
                                DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();
                                deleteSecurityGroupRequest.setSecurityGroupId(securityGroupId);
                                try {
                                    vpcApi.deleteSecurityGroup(deleteSecurityGroupRequest);
                                } catch (ApiException ignored) {
                                    log.info("创建服务器异常 2ignored: {}", messageContent, ignored);
                                }
                            }
                        });
            }
            sysCustomerSubnetMapper.delete(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                    .eq(SysCustomerSubnetEntity::getCustomerInstanceId, instancesEntity.getId()));
            sysCustomerSecurityGroupMapper.delete(Wrappers.lambdaQuery(SysCustomerSecurityGroupEntity.class)
                    .eq(SysCustomerSecurityGroupEntity::getCustomerInstanceId, instancesEntity.getId()));
            // 取消订单，释放资源
            instancesEntity.setStatus(EcsStatusEnum.ERROR);
            sysCustomerInstancesMapper.updateById(instancesEntity);
            try {
                sysOrderService.cancelOrder(instancesEntity.getOrderId());
            }catch (Exception ignored) {
                log.error("创建服务器，取消订单异常：{}", instancesEntity.getOrderId(), ignored);
            }
            if (e instanceof ApiException apiException ) {
                log.error("调用火山创建服务器失败：{} ，失败原因：{}", instancesEntity.getOrderId(), apiException.getResponseBody());
            }else {
                log.error("创建服务器异常：{}", instancesEntity.getOrderId(), e);
            }
        }
    }

    private List<NetworkInterfaceForRunInstancesInput> getNetworkInterfaces(SysCustomerInstancesEntity instancesEntity) throws ApiException {
        VpcApi vpcApi = VoEngineApiClient.getVpcApi(instancesEntity.getRegion().getId());
        String vpcId = sysCustomerVpcService.getCustomerVpcOnInsert(instancesEntity.getCustomerId(), instancesEntity.getRegion()).getVpcId();
        DescribeSubnetsRequest request = new DescribeSubnetsRequest();
        request.setVpcId(vpcId);
        Integer totalCount = vpcApi.describeSubnets(request).getTotalCount()  + 1;
        // 创建子网
        CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest();
        createSubnetRequest.setCidrBlock("172.16." + totalCount + ".0/24");
        createSubnetRequest.setZoneId(instancesEntity.getZoneId());
        createSubnetRequest.setDescription(instancesEntity.getDescription());
        createSubnetRequest.setSubnetName(instancesEntity.getInstanceName() + "-Default");
        createSubnetRequest.vpcId(vpcId);
        String subnetId = vpcApi.createSubnet(createSubnetRequest).getSubnetId();
        String subnetStatus;
        do {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
            DescribeSubnetAttributesRequest subnetAttributesRequest = new DescribeSubnetAttributesRequest();
            subnetAttributesRequest.setSubnetId(subnetId);
            DescribeSubnetAttributesResponse describeSubnetAttributesResponse = vpcApi.describeSubnetAttributes(subnetAttributesRequest);
            subnetStatus = describeSubnetAttributesResponse.getStatus();
        } while (!"Available".equals(subnetStatus));
        SysCustomerSubnetEntity subnetEntity = new SysCustomerSubnetEntity();
        subnetEntity.setOrderId(instancesEntity.getOrderId());
        subnetEntity.setOrderSourceUid(instancesEntity.getOrderSourceUid());
        subnetEntity.setCustomerInstanceId(instancesEntity.getId());
        subnetEntity.setCustomerId(instancesEntity.getCustomerId());
        subnetEntity.setVpcId(vpcId);
        subnetEntity.setSubnetId(subnetId);
        subnetEntity.setSubnetName(createSubnetRequest.getSubnetName());
        subnetEntity.setSubnetDesc(createSubnetRequest.getDescription());
        subnetEntity.setStatus(subnetStatus);
        subnetEntity.setZoneId(instancesEntity.getZoneId());
        subnetEntity.setAvailableIpAddressCount(1);
        sysCustomerSubnetMapper.insert(subnetEntity);

        // 创建安全组
        CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest();
        securityGroupRequest.setProjectName(volEngineConfig.getProjectName());
        securityGroupRequest.setSecurityGroupName(instancesEntity.getInstanceName() + "-Default");
        securityGroupRequest.setDescription(securityGroupRequest.getSecurityGroupName());
        securityGroupRequest.setVpcId(vpcId);
        String securityGroupId = vpcApi.createSecurityGroup(securityGroupRequest).getSecurityGroupId();
        String securityGroupStatus;
        do {
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
            DescribeSecurityGroupAttributesRequest describeSecurityGroupAttributeRequest = new DescribeSecurityGroupAttributesRequest();
            describeSecurityGroupAttributeRequest.setSecurityGroupId(securityGroupId);
            securityGroupStatus = vpcApi.describeSecurityGroupAttributes(describeSecurityGroupAttributeRequest).getStatus();
        } while (!"Available".equals(securityGroupStatus));
        // 添加入向规则
        AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest();
        ingressRequest.setSecurityGroupId(securityGroupId);
        ingressRequest.setProtocol("all");
        ingressRequest.setPortStart(-1);
        ingressRequest.setPortEnd(-1);
        ingressRequest.setCidrIp("0.0.0.0/0");
        ingressRequest.setPolicy("accept");
        vpcApi.authorizeSecurityGroupIngress(ingressRequest);
        SysCustomerSecurityGroupEntity securityGroupEntity = new SysCustomerSecurityGroupEntity();
        securityGroupEntity.setOrderId(instancesEntity.getOrderId());
        securityGroupEntity.setOrderSourceUid(instancesEntity.getOrderSourceUid());
        securityGroupEntity.setCustomerInstanceId(instancesEntity.getId());
        securityGroupEntity.setCustomerId(instancesEntity.getCustomerId());
        securityGroupEntity.setVpcId(vpcId);
        securityGroupEntity.setSecurityGroupId(securityGroupId);
        securityGroupEntity.setSecurityGroupName(securityGroupRequest.getSecurityGroupName());
        securityGroupEntity.setSecurityGroupDesc(securityGroupRequest.getDescription());
        securityGroupEntity.setStatus(securityGroupStatus);
        securityGroupEntity.setRegionId(instancesEntity.getRegion());
        securityGroupEntity.setZoneId(instancesEntity.getZoneId());
        sysCustomerSecurityGroupMapper.insert(securityGroupEntity);

        NetworkInterfaceForRunInstancesInput networkInput = new NetworkInterfaceForRunInstancesInput();
        networkInput.setSubnetId(subnetEntity.getSubnetId());
        networkInput.setSecurityGroupIds(List.of(securityGroupId));
        return List.of(networkInput);
    }


    private List<VolumeForRunInstancesInput> getVolumeForRunInstancesInputs(List<SysCustomerVolumeEntity> customerVolumeList) {
        List<VolumeForRunInstancesInput> volumeReqeustList = new ArrayList<>(customerVolumeList.size());
        for (SysCustomerVolumeEntity volume : customerVolumeList) {
            VolumeForRunInstancesInput instancesInput = new VolumeForRunInstancesInput();
            instancesInput.setSize(volume.getSize());
            instancesInput.setVolumeType(volume.getVolumeType());
            instancesInput.setDeleteWithInstance("true");
            if ("system".equals(volume.getKind())) {
                volumeReqeustList.add(0, instancesInput);
            } else {
                volumeReqeustList.add(instancesInput);
            }
        }
        return volumeReqeustList;
    }

    @Override
    public String messageType() {
        return CacheQueueConstant.CUSTOMER_INSTANCE_CREATE_QUERY_QUEUE_TYPE;
    }
}
