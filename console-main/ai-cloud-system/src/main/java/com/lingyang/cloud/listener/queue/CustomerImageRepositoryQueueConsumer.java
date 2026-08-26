package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.SysCustomerImageRepositoryEntity;
import com.lingyang.cloud.entity.SysCustomerSubnetEntity;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysCustomerMapper;
import com.lingyang.cloud.mapper.SysCustomerSubnetMapper;
import com.lingyang.cloud.mapper.SysImageRepositoryMapper;
import com.lingyang.cloud.service.SysCustomerVpcService;
import com.lingyang.cloud.service.SysOrderService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.lingyang.common.core.utils.IdUtils;
import com.volcengine.ApiException;
import com.volcengine.cr.CrApi;
import com.volcengine.cr.model.*;
import com.volcengine.vpc.VpcApi;
import com.volcengine.vpc.model.CreateSubnetRequest;
import com.volcengine.vpc.model.DescribeSubnetAttributesRequest;
import com.volcengine.vpc.model.DescribeSubnetAttributesResponse;
import com.volcengine.vpc.model.DescribeSubnetsRequest;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/02/17 17:03
 */
@Service
@Slf4j
public class CustomerImageRepositoryQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysImageRepositoryMapper sysImageRepositoryMapper;
    @Resource
    private SysOrderService sysOrderService;
    @Resource
    private VolEngineConfig volEngineConfig;
    @Resource
    private RedisTemplate<String, String> redisTemplate; // 添加RedisTemplate用于分布式锁
    @Resource
    private SysCustomerMapper sysCustomerMapper;
    @Resource
    private SysCustomerVpcService sysCustomerVpcService;
    @Resource
    private SysCustomerSubnetMapper sysCustomerSubnetMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        String lockKey = "customer_image_repository_lock:" + messageContent;
        String lockValue = IdUtils.fastSimpleUUID();
        boolean locked = false;

        try {
            // 尝试获取分布式锁
            locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.MINUTES);
            if (!locked) {
                log.info("订单资源id：{} 已被其他线程处理，跳过本次执行", messageContent);
                return;
            }

            log.info("订单资源id：{}", messageContent);
            SysCustomerImageRepositoryEntity imageRepositoryEntity = sysImageRepositoryMapper.selectOne(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                    .eq(SysCustomerImageRepositoryEntity::getOrderSourceUid, messageContent));
            if (imageRepositoryEntity == null || !imageRepositoryEntity.getStatus().equals(ContainerStatusEnum.CREATING)) {
                return;
            }
            CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
            CreateRegistryRequest request = new CreateRegistryRequest();
            try {
                // 创建镜像仓库实例
                request.setName(imageRepositoryEntity.getInstanceName());
                request.setClientToken(IdUtils.fastSimpleUUID());
                request.setType("Enterprise");
                request.setProject(volEngineConfig.getProjectName());
                log.info("调用创建镜像仓库开始---");
                crApi.createRegistry(request);
                String imageStatus = "Creating";
                do {
                    log.info("未创建成功，再次创建： {}", messageContent);
                    Thread.sleep(10*1000L);
                    try {
                        //查看仓库镜像实例信息
                        ListRegistriesRequest body = new ListRegistriesRequest();
                        FilterForListRegistriesInput filter = new FilterForListRegistriesInput();
                        filter.setNames(List.of(imageRepositoryEntity.getInstanceName()));
                        body.setFilter(filter);
                        ListRegistriesResponse listRegistriesResponse = crApi.listRegistries(body);
                        log.info("未创建成功，再次创建返回： {}  listClustersResponse:{}", messageContent, JSON.toJSONString(listRegistriesResponse));
                        List<ItemForListRegistriesOutput> items = listRegistriesResponse.getItems();
                        if (ObjectUtils.isNotEmpty(items)){
                            ItemForListRegistriesOutput item = items.get(0);
                            log.info("未创建成功，再次创建返回状态： {}  status:{}", messageContent, item.getStatus().getPhase());
                            imageStatus = item.getStatus().getPhase();
                        }
                    }catch (ApiException msg){
                        log.info("创建镜像仓库实例异常: {}", messageContent, msg);
                    }
                }while (!"Running".equals(imageStatus));
                log.info("创建镜像仓库第一步成功");
                try {
                    //创建命名空间
                    CreateNamespaceRequest body = new CreateNamespaceRequest();
                    body.setRegistry(imageRepositoryEntity.getInstanceName());
                    Long customerId = imageRepositoryEntity.getCustomerId();

                    String namespace = imageRepositoryEntity.getInstanceName() + "-namespace";
                    body.setName(namespace);
                    body.setClientToken(IdUtils.fastSimpleUUID());
                    body.setProject(volEngineConfig.getProjectName());
                    crApi.createNamespace(body);
                    imageRepositoryEntity.setNamespace(namespace);
                    imageRepositoryEntity.setStatus(ContainerStatusEnum.RUNNING);
                    log.info("创建命名空间成功");
                    //创建OCI制品仓库
                    CreateRepositoryRequest createRepositoryRequest = new CreateRepositoryRequest();
                    createRepositoryRequest.setRegistry(imageRepositoryEntity.getInstanceName());
                    createRepositoryRequest.setNamespace(namespace);
                    String name = imageRepositoryEntity.getInstanceName() + "oci";
                    createRepositoryRequest.setName(name);
                    createRepositoryRequest.setClientToken(IdUtils.fastSimpleUUID());
                    createRepositoryRequest.setAccessLevel("Public");
                    crApi.createRepository(createRepositoryRequest);
                    imageRepositoryEntity.setOciName(name);
                    log.info("创建创建OCI制品仓库成功");
                    //更新实例公网入口(默认公网)
                    UpdatePublicEndpointRequest updatePublicEndpointRequest = new UpdatePublicEndpointRequest();
                    updatePublicEndpointRequest.setRegistry(imageRepositoryEntity.getInstanceName());
                    updatePublicEndpointRequest.setEnabled(true);
                    crApi.updatePublicEndpoint(updatePublicEndpointRequest);
                    log.info("更新实例公网入口(默认公网)成功");
                    //更新VPC访问入口
                    UpdateVpcEndpointRequest body1 = new UpdateVpcEndpointRequest();
                    body1.setRegistry(imageRepositoryEntity.getInstanceName());
                    VpcForUpdateVpcEndpointInput vpcForUpdateVpcEndpointInput = new VpcForUpdateVpcEndpointInput();
                    String vpcId = sysCustomerVpcService.getCustomerVpcOnInsert(imageRepositoryEntity.getCustomerId(), SourceRegionsEnum.CN_BEIJING).getVpcId();
                    List<SysCustomerSubnetEntity> sysCustomerSubnetEntities = sysCustomerSubnetMapper.selectList(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                            .eq(SysCustomerSubnetEntity::getVpcId, vpcId));
                    if (ObjectUtils.isEmpty(sysCustomerSubnetEntities)){
                        log.info("镜像仓库开始创建子网------");
                        // 创建子网
                        VpcApi vpcApi = VoEngineApiClient.getVpcApi(SourceRegionsEnum.CN_BEIJING.getId());
                        DescribeSubnetsRequest request2 = new DescribeSubnetsRequest();
                        request2.setVpcId(vpcId);
                        Integer totalCount = vpcApi.describeSubnets(request2).getTotalCount();
                        CreateSubnetRequest createSubnetRequest = new CreateSubnetRequest();
                        createSubnetRequest.setCidrBlock("172.16." + totalCount + ".0/24");
                        createSubnetRequest.setZoneId("cn-beijing-a");
                        createSubnetRequest.setSubnetName(imageRepositoryEntity.getInstanceName() + "-Default");
                        createSubnetRequest.vpcId(vpcId);
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
                        subnetEntity.setOrderId(imageRepositoryEntity.getOrderId());
                        subnetEntity.setOrderSourceUid(imageRepositoryEntity.getOrderSourceUid());
                        subnetEntity.setCustomerInstanceId(customerId);//先存客户的id进去
                        subnetEntity.setCustomerId(customerId);
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
                    }
                    vpcForUpdateVpcEndpointInput.setVpcId(vpcId);
                    body1.setVpcs(List.of(vpcForUpdateVpcEndpointInput));
                    crApi.updateVpcEndpoint(body1);
                    log.info("更新VPC访问入口成功");
                    sysImageRepositoryMapper.updateById(imageRepositoryEntity);
                }catch (Exception e){
                    log.info("创建镜像仓库第二步异常");
                    log.info("删除镜像仓库");
                    try {
                        DeleteRegistryRequest body = new DeleteRegistryRequest();
                        body.setName(imageRepositoryEntity.getInstanceName());
                        body.setDeleteImmediately(true);
                        crApi.deleteRegistry(body);
                    }catch (Exception ignored) {
                        log.info("删除镜像仓库失败");
                    }
                }
            }catch (Exception e){
                log.info("创建镜像仓库实例异常: {}", messageContent, e);
                // 取消订单，释放资源
                imageRepositoryEntity.setStatus(ContainerStatusEnum.ERROR);
                sysImageRepositoryMapper.updateById(imageRepositoryEntity);
                try {
                    sysOrderService.cancelOrder(imageRepositoryEntity.getOrderId());
                }catch (Exception ignored) {
                    log.error("创建镜像仓库，取消订单异常：{}", imageRepositoryEntity.getOrderId(), ignored);
                }
                if (e instanceof ApiException apiException ) {
                    log.error("调用火山创建镜像仓库失败：{} ，失败原因：{}", imageRepositoryEntity.getOrderId(), apiException.getResponseBody());
                }else {
                    log.error("创建镜像仓库异常：{}", imageRepositoryEntity.getOrderId(), e);
                }
            }
        } finally {
            // 释放分布式锁
            if (locked) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    @Override
    public String messageType() {
        return CacheQueueConstant.CUSTOMER_IMAGE_REPOSITORY_QUERY_QUEUE_TYPE;
    }
}
