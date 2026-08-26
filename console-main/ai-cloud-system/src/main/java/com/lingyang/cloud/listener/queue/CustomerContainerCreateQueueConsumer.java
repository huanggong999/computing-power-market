package com.lingyang.cloud.listener.queue;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.EcsStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.vo.container.SysContainerVO;
import com.lingyang.cloud.service.SysCustomerVpcService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.cache.CacheQueueService;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.utils.IdUtils;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.*;
import com.volcengine.storageebs.StorageEbsApi;
import com.volcengine.storageebs.model.DescribeVolumesRequest;
import com.volcengine.storageebs.model.VolumeForDescribeVolumesOutput;
import com.volcengine.vke.VkeApi;
import com.volcengine.vke.model.*;
import com.volcengine.vpc.VpcApi;
import com.volcengine.vpc.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/02/11
 */
@Service
@Slf4j
public class CustomerContainerCreateQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;
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
    @Resource
    private CacheQueueService cacheQueueService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        log.info("订单资源id：{}", messageContent);
        SysCustomerContainerEntity containerEntity = sysCustomerContainerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getOrderSourceUid, messageContent));
        if (containerEntity == null || !containerEntity.getStatus().equals(ContainerStatusEnum.CREATING)) {
            return;
        }
        List<SysCustomerVolumeEntity> customerVolumeList = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                .eq(SysCustomerVolumeEntity::getOrderSourceUid, messageContent));
        SysCustomerInstancesEntity customerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                .eq(SysCustomerInstancesEntity::getOrderSourceUid, messageContent));
        VkeApi vkeApi = VoEngineApiClient.getVkeApi(SourceRegionsEnum.CN_BEIJING.getId());
        log.info("创建查询地域： {} {}", messageContent, JSON.toJSONString(vkeApi));
        CreateClusterRequest request = new CreateClusterRequest();
        SysContainerVO vpc = getVpc(containerEntity);
        try {
            // 创建容器器
            request.setProjectName(volEngineConfig.getProjectName());
            request.setName(containerEntity.getClusterName());
            request.setKubernetesVersion(containerEntity.getKubernetesVersion());
            ClusterConfigForCreateClusterInput clusterConfig = new ClusterConfigForCreateClusterInput();
            String subnetId = vpc.getCustomerSubnet().getSubnetId();
            clusterConfig.setSubnetIds(List.of(subnetId));
            clusterConfig.setApiServerPublicAccessEnabled(containerEntity.getApiServerPublicAccessEnabled());
            clusterConfig.setResourcePublicAccessDefaultEnabled(containerEntity.getResourcePublicAccessDefaultEnabled());
            request.setClusterConfig(clusterConfig);
            PodsConfigForCreateClusterInput podsConfig = new PodsConfigForCreateClusterInput();
            podsConfig.setPodNetworkMode(PodsConfigForCreateClusterInput.PodNetworkModeEnum.VPCCNISHARED);
            VpcCniConfigForCreateClusterInput vpcCniConfig = new VpcCniConfigForCreateClusterInput();
            vpcCniConfig.setSubnetIds(List.of(subnetId));
            podsConfig.setVpcCniConfig(vpcCniConfig);
            request.setPodsConfig(podsConfig);
            request.setServicesConfig(new ServicesConfigForCreateClusterInput().serviceCidrsv4(List.of("192.168.0.0/17")));
            request.setClientToken(IdUtils.fastSimpleUUID());
            log.info("创建集群请求： {} {}", messageContent, JSON.toJSONString(request));
            String clusterId = vkeApi.createCluster(request).getId();
            log.info("创建集群id:{}", clusterId);
            String clustersStatus = "Creating";
            String nodePoolStatus = "Creating";
            ItemForListClustersOutput itemForListClustersOutput = null;
            do {
                log.info("未创建成功，再次创建： {} {}", messageContent, clusterId);
                Thread.sleep(1000L);
                try {
                    // 容器信息
                    ListClustersRequest clustersRequest = new ListClustersRequest();
                    FilterForListClustersInput filter = new FilterForListClustersInput();
                    filter.setIds(List.of(clusterId));
                    clustersRequest.filter(filter);
                    ListClustersResponse listClustersResponse = vkeApi.listClusters(clustersRequest);
                    log.info("未创建成功，再次创建返回： {} {} listClustersResponse:{}", messageContent, clusterId, JSON.toJSONString(listClustersResponse));
                    itemForListClustersOutput = listClustersResponse.getItems().get(0);
                    if (ObjectUtils.isNotEmpty(itemForListClustersOutput)) {

                        log.info("未创建成功，再次创建返回状态： {} {} status:{}", messageContent, clusterId, itemForListClustersOutput.getStatus().getPhase());
                        clustersStatus = itemForListClustersOutput.getStatus().getPhase();
                    }
                } catch (ApiException ignored) {
                    log.info("创建容器异常 ignored: {}", messageContent, ignored);
                }
            } while (!"Running".equals(clustersStatus));
            log.info("容器第一步创建成功");
            containerEntity.setStatus(ContainerStatusEnum.getDescByVolcengineDesc(clustersStatus));
            containerEntity.setClusterId(clusterId);
            containerEntity.setCustomerSubnetId(vpc.getCustomerSubnet().getId());

            //创建节点池
            CreateNodePoolRequest body = new CreateNodePoolRequest();
            body.setClusterId(clusterId);
            body.setName(containerEntity.getNodePoolName());
            NodeConfigForCreateNodePoolInput nodeConfig = new NodeConfigForCreateNodePoolInput();
            nodeConfig.setProjectName(volEngineConfig.getProjectName());
            nodeConfig.setInstanceTypeIds(List.of(customerInstancesEntity.getEcsScale()));
            nodeConfig.setSubnetIds(List.of(subnetId));
            nodeConfig.setImageId(customerInstancesEntity.getImageId());
            DataVolumeForCreateNodePoolInput dataVolume = new DataVolumeForCreateNodePoolInput();
            dataVolume.setType(DataVolumeForCreateNodePoolInput.TypeEnum.ESSD_PL0);
            dataVolume.setSize(20);
            nodeConfig.setDataVolumes(List.of(dataVolume));
            SystemVolumeForCreateNodePoolInput systemVolume = new SystemVolumeForCreateNodePoolInput();
            systemVolume.setType(SystemVolumeForCreateNodePoolInput.TypeEnum.ESSD_FLEXPL);
            systemVolume.setSize(40);
            nodeConfig.setSystemVolume(systemVolume);
            SecurityForCreateNodePoolInput security = new SecurityForCreateNodePoolInput();
            String securityGroupId = vpc.getSecurityGroupEntity().getSecurityGroupId();
            security.setSecurityGroupIds(List.of(securityGroupId));
            if (containerEntity.getIsOpenSecurityHardening()) {
                security.setSecurityStrategies(List.of(SecurityForCreateNodePoolInput.SecurityStrategiesEnum.HIDS));
            }
            LoginForCreateNodePoolInput login = new LoginForCreateNodePoolInput();
            String password = customerInstancesEntity.getPassword();
            String encodedPassword = Base64.getEncoder().encodeToString(password.getBytes());
            login.setPassword(encodedPassword);
            security.setLogin(login);
            nodeConfig.setSecurity(security);
            nodeConfig.setAdditionalContainerStorageEnabled(true);

            SourceChargeTypeEnum chargeType = customerInstancesEntity.getChargeType();
            switch (chargeType) {
                case POSTPAID_BY_HOUR ->
                        nodeConfig.setInstanceChargeType(NodeConfigForCreateNodePoolInput.InstanceChargeTypeEnum.POSTPAID);
                case POSTPAID_BY_MONTH -> {
                    nodeConfig.setInstanceChargeType(NodeConfigForCreateNodePoolInput.InstanceChargeTypeEnum.PREPAID);
                    nodeConfig.setPeriod(customerInstancesEntity.getDuration());
                }
                case POSTPAID_BY_YEAR -> {
                    nodeConfig.setInstanceChargeType(NodeConfigForCreateNodePoolInput.InstanceChargeTypeEnum.PREPAID);
                    nodeConfig.setPeriod(customerInstancesEntity.getDuration() * 12);
                }
            }
            nodeConfig.setAutoRenew(false);
            body.setNodeConfig(nodeConfig);
            body.setClientToken(IdUtils.fastSimpleUUID());
            String nodePoolId = vkeApi.createNodePool(body).getId();
            ItemForListNodePoolsOutput itemForListNodePoolsOutput = null;
            do {
                log.info("节点池未创建成功，再次创建： {} {}", messageContent, nodePoolId);
                Thread.sleep(1000L);
                try {
                    //节点池信息
                    ListNodePoolsRequest nodePoolsRequest = new ListNodePoolsRequest();
                    FilterForListNodePoolsInput filter = new FilterForListNodePoolsInput();
                    filter.setClusterIds(List.of(clusterId));
                    filter.setIds(List.of(nodePoolId));
                    nodePoolsRequest.filter(filter);
                    ListNodePoolsResponse listNodePoolsResponse = vkeApi.listNodePools(nodePoolsRequest);
                    log.info("节点池未创建成功，再次创建返回： {} {} listNodePoolsResponse:{}", messageContent, nodePoolId, JSON.toJSONString(listNodePoolsResponse));
                    itemForListNodePoolsOutput = listNodePoolsResponse.getItems().get(0);
                    if (ObjectUtils.isNotEmpty(itemForListNodePoolsOutput)) {

                        log.info("节点池未创建成功，再次创建返回状态： {} {} status:{}", messageContent, clusterId, itemForListNodePoolsOutput.getStatus().getPhase());
                        nodePoolStatus = itemForListClustersOutput.getStatus().getPhase();
                    }

                } catch (ApiException ignored) {
                    log.info("创建容器节点池异常 ignored: {}", messageContent, ignored);
                    //删除集群
                    try {
                        vkeApi.deleteCluster(new DeleteClusterRequest().id(clusterId));
                    } catch (ApiException msg) {
                        log.info("删除集群异常 msg: {}", messageContent, msg);
                    }

                }
            } while (!"Running".equals(nodePoolStatus));
            containerEntity.setNodePoolId(nodePoolId);
            sysCustomerContainerMapper.updateById(containerEntity);
            Integer nodePoolNumber = containerEntity.getNodePoolNumber();
            List<String> instanceIds = null;
            //创建云服务器实例
            log.info("容器创建云服务器实例开始-----------");
            EcsApi ecsApi = VoEngineApiClient.getEcsApi(customerInstancesEntity.getRegion().getId());
            log.info("创建查询地域： {} {}", messageContent, JSON.toJSONString(ecsApi));
            RunInstancesRequest request2 = new RunInstancesRequest();
            try {
                // 创建服务器
                request2.setUserData(new String(Base64.getEncoder().encode(messageContent.getBytes(StandardCharsets.UTF_8))));
                request2.setAutoRenew(false);
                request2.setCount(nodePoolNumber);
                request2.setDescription(customerInstancesEntity.getDescription());
                request2.setHostname(customerInstancesEntity.getHostName());
                request2.setImageId(customerInstancesEntity.getImageId());
                request2.setInstallRunCommandAgent(customerInstancesEntity.getCommandAgent());
                request2.setInstanceChargeType(customerInstancesEntity.getChargeType().getVolcengineDesc());
                request2.setInstanceName(ObjectUtils.defaultIfNull(customerInstancesEntity.getInstanceName(), containerEntity.getClusterName() + "_ecs"));
                request2.setInstanceTypeId(customerInstancesEntity.getEcsScale());
                request2.setMinCount(1);
                request2.setPassword(customerInstancesEntity.getPassword());
                request2.period(customerInstancesEntity.getDuration());
                request2.setPeriodUnit(customerInstancesEntity.getDurationUnit().getVolcengineDesc());
                request2.setSpotStrategy("NoSpot");
                request2.setZoneId(customerInstancesEntity.getZoneId());
                request2.setClientToken(IdUtils.fastSimpleUUID());
                request2.setProjectName(volEngineConfig.getProjectName());
                SysCustomerEipEntity eipEntity = sysCustomerEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEipEntity.class)
                        .eq(SysCustomerEipEntity::getOrderSourceUid, messageContent));
                EipAddressForRunInstancesInput eipInput = new EipAddressForRunInstancesInput();
                eipInput.setISP(eipEntity.getIsp());
                eipInput.setBandwidthMbps(eipEntity.getBandwidth());
                eipInput.setChargeType(eipEntity.getBillingType());
                eipInput.setReleaseWithInstance(true);
                request2.setEipAddress(eipInput);
                request2.setVolumes(getVolumeForRunInstancesInputs(customerVolumeList));
                NetworkInterfaceForRunInstancesInput networkInput = new NetworkInterfaceForRunInstancesInput();
                networkInput.setSubnetId(vpc.getCustomerSubnet().getSubnetId());
                networkInput.setSecurityGroupIds(List.of(vpc.getSecurityGroupEntity().getSecurityGroupId()));
                request2.setNetworkInterfaces(List.of(networkInput));
                log.info("容器创建云服务器参数： {} {}", messageContent, JSON.toJSONString(request2));
                instanceIds = ecsApi.runInstances(request2).getInstanceIds();
                log.info("容器创建云服务器id： {} {}", messageContent, instanceIds);
                String status = "CREATING";
                InstanceForDescribeInstancesOutput instancesOutput = null;
                do {
                    log.info("容器云服务器未创建成功，再次创建： {} {}", messageContent, instanceIds);
                    Thread.sleep(1000L);
                    try {
                        // 实列信息
                        DescribeInstancesRequest ecsQueryRequest = new DescribeInstancesRequest();
                        ecsQueryRequest.setInstanceIds(instanceIds);
                        ecsQueryRequest.setMaxResults(1);
                        DescribeInstancesResponse ecsResponse = ecsApi.describeInstances(ecsQueryRequest);
                        log.info("容器云服务器未创建成功，再次创建返回： {} {} ecsResponse:{}", messageContent, instanceIds, JSON.toJSONString(ecsResponse));
                        instancesOutput = ecsResponse.getInstances().get(0);
                        if (ObjectUtils.isNotEmpty(instancesOutput)) {
                            EipAddressForDescribeInstancesOutput eipAddress = instancesOutput.getEipAddress();
                            if (ObjectUtils.isNotEmpty(eipAddress)) {
                                eipEntity.setEipAddress(eipAddress.getIpAddress());
                                eipEntity.setEipId(eipAddress.getAllocationId());
                                eipEntity.setEipName(eipAddress.getAllocationId());
                            }
                            log.info("容器云服务器未创建成功，再次创建返回状态： {} {} status:{}", messageContent, instanceIds, instancesOutput.getStatus());
                            status = instancesOutput.getStatus();
                        }
                    } catch (ApiException ignored) {
                        log.info("容器创建服务器异常 msg: {}", ignored.getMessage());
                        log.info("容器创建服务器异常 ignored: {}", messageContent, ignored);
                    }
                } while (!"RUNNING".equals(status));
                log.info("容器创建云服务器成功");
                customerInstancesEntity.setStatus(EcsStatusEnum.valueOf(status));
                customerInstancesEntity.setInstanceId(instanceIds.get(0));
                eipEntity.setInstanceId(instanceIds.get(0));
                eipEntity.setCustomerInstanceId(customerInstancesEntity.getId());
                // 获取云盘信息
                StorageEbsApi storageEbsApi = VoEngineApiClient.getStorageEbsApi(customerInstancesEntity.getRegion().getId());
                DescribeVolumesRequest volumesRequest = new DescribeVolumesRequest();
                volumesRequest.setInstanceId(instanceIds.get(0));
                volumesRequest.setPageSize(100);
                volumesRequest.setZoneId(customerInstancesEntity.getZoneId());
                List<VolumeForDescribeVolumesOutput> volumes = storageEbsApi.describeVolumes(volumesRequest).getVolumes();
                do {
                    log.info("查询云盘信息未成功，再次查询");
                    try {
                        Thread.sleep(30*1000L);
                        volumes = storageEbsApi.describeVolumes(volumesRequest).getVolumes();
                        log.info("查询云盘信息： {} {} volumes:{}", messageContent, instanceIds, JSON.toJSONString(volumes));
                    } catch (ApiException ignored) {
                        log.info("容器查询云盘异常 msg: {}", ignored.getMessage());
                        log.info("容器查询云盘异常 ignored: {}", messageContent, ignored);
                    }
                }while (volumes.isEmpty());
                Map<String, List<VolumeForDescribeVolumesOutput>> kindVolumeMap = volumes.stream()
                        .collect(Collectors.groupingBy(VolumeForDescribeVolumesOutput::getKind));
                AtomicInteger volumeCount = new AtomicInteger(0);
                List<String> finalInstanceIds = instanceIds;
                customerVolumeList.forEach(e -> {
                    List<VolumeForDescribeVolumesOutput> volumesOutputs = kindVolumeMap.get(e.getKind());
                    if ("system".equals(e.getKind())) {
                        e.setVolumeId(volumesOutputs.get(0).getVolumeId());
                    } else {
                        e.setVolumeId(volumesOutputs.get(volumeCount.getAndIncrement()).getVolumeId());
                    }
                    e.setCustomerId(customerInstancesEntity.getCustomerId());
                    e.setInstanceId(finalInstanceIds.get(0));
                    e.setCustomerInstanceId(customerInstancesEntity.getId());
                });
                sysCustomerVolumeMapper.batchUpdateById(customerVolumeList);
                sysCustomerEipMapper.updateById(eipEntity);
                sysCustomerInstancesMapper.updateById(customerInstancesEntity);
                for (int i = 1; i < nodePoolNumber; i++) {
                    SysCustomerInstancesEntity entity = BeanUtil.copyProperties(customerInstancesEntity, SysCustomerInstancesEntity.class, "id");
                    entity.setInstanceId(instanceIds.get(i));
                    sysCustomerInstancesMapper.insert(entity);
                    List<SysCustomerVolumeEntity> sysCustomerVolumeEntities = BeanUtil.copyToList(customerVolumeList, SysCustomerVolumeEntity.class);
                    sysCustomerVolumeEntities.forEach(e -> {
                        e.setCustomerId(entity.getCustomerId());
                        e.setInstanceId(entity.getInstanceId());
                        e.setCustomerInstanceId(entity.getId());
                        e.setId(null);
                    });
                    sysCustomerVolumeMapper.batchInsert(sysCustomerVolumeEntities);
                }
                log.info("修改云盘成功");
            } catch (ApiException e) {
                log.error("容器创建服务器异常: {}", messageContent, e);
            } catch (Exception e) {
                log.error("容器创建服务器异常: {}", messageContent, e);
                log.error("容器创建服务器异常msg: {}", e.getMessage());
            }

            //添加云服务器到节点池
            try {
                log.info("添加云服务器到节点池延迟1分钟");
                Thread.sleep(60 * 1000L);
                log.info("添加云服务器到节点池: {}", messageContent);
                CreateNodesRequest body2 = new CreateNodesRequest();
                body2.setClientToken(IdUtils.simpleUUID());
                body2.setClusterId(clusterId);
                body2.setInstanceIds(instanceIds);
                body2.setImageId(customerInstancesEntity.getImageId());
                body2.setNodePoolId(nodePoolId);
                log.info("添加云服务器到节点池: {} body2:{}", messageContent, JSON.toJSONString(body2));
                CreateNodesResponse nodes = vkeApi.createNodes(body2);
                log.info("添加云服务器到节点池: {} nodes:{}", messageContent, JSON.toJSONString(nodes));
            } catch (Exception e) {
                log.info("添加云服务器到节点池失败,msg:{}", e.getMessage());
                log.error("添加云服务器到节点池异常: {}", messageContent, e);
            }
            //安装组件 TODO 暂时不装
            //新增nat网关id和负载均衡id的延迟队列
            log.info("新增nat网关id和负载均衡id的延迟队列");
            cacheQueueService.addDelayQueue(CacheQueueConstant.NAT_GATEWAY_ID_QUEUE_TYPE, containerEntity.getClusterId(), 5);
        } catch (Exception e) {
            log.info("创建容器异常: {}", messageContent, e);
            // 删除创建的子网和安全组
            SysCustomerSubnetEntity sysCustomerSubnetEntity = sysCustomerSubnetMapper.selectOne(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                    .eq(SysCustomerSubnetEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId())
                    .last("FOR UPDATE")); // 添加FOR UPDATE明确锁定资源
            log.info("需要删除的子网: {}", sysCustomerSubnetEntity);

            if (ObjectUtils.isNotEmpty(sysCustomerSubnetEntity)) {
                Optional.of(sysCustomerSubnetEntity.getSubnetId())
                        .ifPresent(subnetId -> {
                            VpcApi vpcApi = VoEngineApiClient.getVpcApi(SourceRegionsEnum.CN_BEIJING.getId());
                            DeleteSubnetRequest deleteSubnetRequest = new DeleteSubnetRequest();
                            deleteSubnetRequest.setSubnetId(subnetId);
                            try {
                                vpcApi.deleteSubnet(deleteSubnetRequest);
                            } catch (ApiException ignored) {
                                log.info("创建容器异常 1ignored: {}", messageContent, ignored);
                            }
                        });
                SysCustomerSecurityGroupEntity sysCustomerSecurityGroupEntity = sysCustomerSecurityGroupMapper.selectOne(Wrappers.lambdaQuery(SysCustomerSecurityGroupEntity.class)
                        .eq(SysCustomerSecurityGroupEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId()));
                log.info("需要删除的安全组: {}", sysCustomerSecurityGroupEntity);
                Optional.of(sysCustomerSecurityGroupEntity.getSecurityGroupId())
                        .ifPresent(securityGroupId -> {
                            VpcApi vpcApi = VoEngineApiClient.getVpcApi(SourceRegionsEnum.CN_BEIJING.getId());
                            DeleteSecurityGroupRequest deleteSecurityGroupRequest = new DeleteSecurityGroupRequest();
                            deleteSecurityGroupRequest.setSecurityGroupId(securityGroupId);
                            try {
                                vpcApi.deleteSecurityGroup(deleteSecurityGroupRequest);
                            } catch (ApiException ignored) {
                                log.info("创建容器异常 2ignored: {}", messageContent, ignored);
                            }
                        });
            }
            log.info("------------");
            sysCustomerSubnetMapper.delete(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                    .eq(SysCustomerSubnetEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId()));
            sysCustomerSecurityGroupMapper.delete(Wrappers.lambdaQuery(SysCustomerSecurityGroupEntity.class)
                    .eq(SysCustomerSecurityGroupEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId()));
            sysCustomerInstancesMapper.update(null, Wrappers.lambdaUpdate(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getId, containerEntity.getCustomerInstancesId())
                    .set(SysCustomerInstancesEntity::getStatus, EcsStatusEnum.ERROR));
            // 取消订单，释放资源
            containerEntity.setStatus(ContainerStatusEnum.ERROR);
            sysCustomerContainerMapper.updateById(containerEntity);
            log.info("+++++++++++++");
            try {
                sysOrderService.cancelOrder(containerEntity.getOrderId());
            } catch (Exception ignored) {
                log.error("创建容器，取消订单异常：{}", containerEntity.getOrderId(), ignored);
            }
            if (e instanceof ApiException apiException) {
                log.error("调用火山创建容器失败：{} ，失败原因：{}", containerEntity.getOrderId(), apiException.getResponseBody());
            } else {
                log.error("创建容器异常：{}", containerEntity.getOrderId(), e);
            }
        }
    }

    public SysContainerVO getVpc(SysCustomerContainerEntity containerEntity) throws ApiException {
        log.info("创建容器，获取vpc信息：{}", containerEntity.getOrderId());
        SysContainerVO vo = new SysContainerVO();
        VpcApi vpcApi = VoEngineApiClient.getVpcApi(SourceRegionsEnum.CN_BEIJING.getId());
        String vpcId = sysCustomerVpcService.getCustomerVpcOnInsert(containerEntity.getCustomerId(), SourceRegionsEnum.CN_BEIJING).getVpcId();
        List<SysCustomerSubnetEntity> sysCustomerSubnetEntities = sysCustomerSubnetMapper.selectList(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                .eq(SysCustomerSubnetEntity::getVpcId, vpcId));
        if (ObjectUtils.isEmpty(sysCustomerSubnetEntities)) {
            DescribeSubnetsRequest request = new DescribeSubnetsRequest();
            request.setVpcId(vpcId);
            Integer totalCount = vpcApi.describeSubnets(request).getTotalCount();
            // 创建子网
            CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest();
            createSubnetRequest.setCidrBlock("172.16." + totalCount + ".0/24");
            createSubnetRequest.setZoneId("cn-beijing-a");
            createSubnetRequest.setSubnetName(containerEntity.getClusterName() + "-Default");
            createSubnetRequest.vpcId(vpcId);
            log.info("创建的子网传参：{}", createSubnetRequest);
            String subnetId = vpcApi.createSubnet(createSubnetRequest).getSubnetId();
            log.info("创建的子网id：{}", subnetId);
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
                log.info("创建的子网状态：{}", subnetStatus);
            } while (!"Available".equals(subnetStatus));
            SysCustomerSubnetEntity subnetEntity = new SysCustomerSubnetEntity();
            subnetEntity.setOrderId(containerEntity.getOrderId());
            subnetEntity.setOrderSourceUid(containerEntity.getOrderSourceUid());
            subnetEntity.setCustomerInstanceId(containerEntity.getCustomerInstancesId());
            subnetEntity.setCustomerId(containerEntity.getCustomerId());
            subnetEntity.setVpcId(vpcId);
            subnetEntity.setSubnetId(subnetId);
            subnetEntity.setSubnetName(createSubnetRequest.getSubnetName());
            subnetEntity.setSubnetDesc(createSubnetRequest.getDescription());
            subnetEntity.setStatus(subnetStatus);
            subnetEntity.setZoneId("cn-beijing-a");
            subnetEntity.setAvailableIpAddressCount(1);
            int insert = sysCustomerSubnetMapper.insert(subnetEntity);
            if (insert == 1) {
                log.info("创建子网成功：{}", subnetId);
            } else {
                log.info("创建子网失败：{}", subnetId);
            }
            vo.setCustomerSubnet(subnetEntity);
        } else {
            vo.setCustomerSubnet(sysCustomerSubnetEntities.get(0));
        }
        List<SysCustomerSecurityGroupEntity> sysCustomerSecurityGroupEntities = sysCustomerSecurityGroupMapper.selectList(Wrappers.lambdaQuery(SysCustomerSecurityGroupEntity.class)
                .eq(SysCustomerSecurityGroupEntity::getVpcId, vpcId));
        if (ObjectUtils.isEmpty(sysCustomerSecurityGroupEntities)) {
            // 创建安全组
            CreateSecurityGroupRequest securityGroupRequest = new CreateSecurityGroupRequest();
            securityGroupRequest.setProjectName(volEngineConfig.getProjectName());
            securityGroupRequest.setSecurityGroupName(containerEntity.getClusterName() + "-Default");
            securityGroupRequest.setDescription(securityGroupRequest.getSecurityGroupName());
            securityGroupRequest.setVpcId(vpcId);
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
            String securityGroupId = vpcApi.createSecurityGroup(securityGroupRequest).getSecurityGroupId();
            log.info("创建的安全组id：{}", securityGroupId);
            String securityGroupStatus;
            do {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                DescribeSecurityGroupAttributesRequest describeSecurityGroupAttributeRequest = new DescribeSecurityGroupAttributesRequest();
                describeSecurityGroupAttributeRequest.setSecurityGroupId(securityGroupId);
                securityGroupStatus = vpcApi.describeSecurityGroupAttributes(describeSecurityGroupAttributeRequest).getStatus();
                log.info("安全组状态：{}", securityGroupStatus);
            } while (!"Available".equals(securityGroupStatus));
            // 添加入向规则
            AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest();
            ingressRequest.setSecurityGroupId(securityGroupId);
            ingressRequest.setProtocol("all");
            ingressRequest.setPortStart(-1);
            ingressRequest.setPortEnd(-1);
            ingressRequest.setCidrIp("0.0.0.0/0");
            ingressRequest.setPolicy("accept");
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
            vpcApi.authorizeSecurityGroupIngress(ingressRequest);
            SysCustomerSecurityGroupEntity securityGroupEntity = new SysCustomerSecurityGroupEntity();
            securityGroupEntity.setOrderId(containerEntity.getOrderId());
            securityGroupEntity.setOrderSourceUid(containerEntity.getOrderSourceUid());
            securityGroupEntity.setCustomerInstanceId(containerEntity.getCustomerInstancesId());
            securityGroupEntity.setCustomerId(containerEntity.getCustomerId());
            securityGroupEntity.setVpcId(vpcId);
            securityGroupEntity.setSecurityGroupId(securityGroupId);
            securityGroupEntity.setSecurityGroupName(securityGroupRequest.getSecurityGroupName());
            securityGroupEntity.setSecurityGroupDesc(securityGroupRequest.getDescription());
            securityGroupEntity.setStatus(securityGroupStatus);
            securityGroupEntity.setRegionId(SourceRegionsEnum.CN_BEIJING);
            securityGroupEntity.setZoneId("cn-beijing-a");
            int insert1 = sysCustomerSecurityGroupMapper.insert(securityGroupEntity);
            if (insert1 == 1) {
                log.info("安全组创建成功");
            } else {
                log.info("安全组创建失败");
            }
            vo.setSecurityGroupEntity(securityGroupEntity);
        } else {
            vo.setSecurityGroupEntity(sysCustomerSecurityGroupEntities.get(0));
        }
        return vo;
    }

    private List<DataVolumeForCreateNodePoolInput> dataVolumes(List<SysCustomerVolumeEntity> customerVolumeList) {
        List<DataVolumeForCreateNodePoolInput> dataVolumes = new ArrayList<>(customerVolumeList.size());
        for (SysCustomerVolumeEntity volume : customerVolumeList) {
            DataVolumeForCreateNodePoolInput dataVolumeForCreateNodePoolInput = new DataVolumeForCreateNodePoolInput();
            dataVolumeForCreateNodePoolInput.setSize(volume.getSize());
            dataVolumeForCreateNodePoolInput.setType(DataVolumeForCreateNodePoolInput.TypeEnum.fromValue(volume.getVolumeType()));
            if ("system".equals(volume.getKind())) {
                dataVolumes.add(0, dataVolumeForCreateNodePoolInput);
            } else {
                dataVolumes.add(dataVolumeForCreateNodePoolInput);
            }
        }
        return dataVolumes;
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
        return CacheQueueConstant.CUSTOMER_CONTAINER_CREATE_QUERY_QUEUE_TYPE;
    }
}
