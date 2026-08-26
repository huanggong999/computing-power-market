package com.lingyang.cloud.api.service.pc.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcContainerDetailsDTO;
import com.lingyang.cloud.api.model.dto.PcContainerListDTO;
import com.lingyang.cloud.api.model.dto.PcContainerOverviewDTO;
import com.lingyang.cloud.api.model.vo.PcContainerImageVO;
import com.lingyang.cloud.api.model.vo.PcContainerListVO;
import com.lingyang.cloud.api.service.pc.PcContainerService;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.source.ContainerStatusEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.DateUtils;
import com.lingyang.common.core.utils.Optional;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.DescribeInstancesRequest;
import com.volcengine.ecs.model.InstanceForDescribeInstancesOutput;
import com.volcengine.vke.VkeApi;
import com.volcengine.vke.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.lingyang.cloud.enums.source.ContainerStatusEnum.getDescByVolcengineDesc;
import static com.lingyang.cloud.enums.source.SourceRegionsEnum.CN_BEIJING;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 11:31
 */
@Service
@Slf4j
public class PcContainerServiceImpl implements PcContainerService {

    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;
    @Resource
    private SysCustomerSubnetMapper sysCustomerSubnetMapper;
    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;
    @Resource
    private SysCustomerSecurityGroupMapper sysCustomerSecurityGroupMapper;
    @Resource
    private SysImageMapper sysImageMapper;
    @Resource
    private SysEcsMapper sysEcsMapper;
    @Resource
    private SysCustomerVpcMapper sysCustomerVpcMapper;

    @Override
    public PcContainerOverviewDTO getContainerOverview() {
        PcContainerOverviewDTO pcContainerOverviewDTO = new PcContainerOverviewDTO();
        Long userId = SecurityContext.getUserInfo().getUserId();
        List<SysCustomerContainerEntity> list = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getCustomerId, userId));
        if (ObjectUtils.isEmpty(list)){
            return new PcContainerOverviewDTO();
        }
        int normalNumber = list.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.RUNNING)).toList().size();
        int containerAbnormalNumber = list.stream().filter(i -> i.getStatus().equals(ContainerStatusEnum.ERROR)).toList().size();
        list.stream().map(SysCustomerContainerEntity::getNodePoolNumber).reduce(Integer::sum).ifPresent(pcContainerOverviewDTO::setNodePoolTotal);
        int containerTotal = list.size();
        pcContainerOverviewDTO.setContainerTotal(containerTotal);
        pcContainerOverviewDTO.setContainerNormalNumber(normalNumber);
        pcContainerOverviewDTO.setContainerAbnormalNumber(containerAbnormalNumber);
        pcContainerOverviewDTO.setContainerOtherNumber(containerTotal - normalNumber - containerAbnormalNumber);
        VkeApi vkeApi = VoEngineApiClient.getVkeApi(CN_BEIJING.getId());
        if (containerTotal == containerAbnormalNumber){
            pcContainerOverviewDTO.setNodePoolNormalNumber(0);
            pcContainerOverviewDTO.setNodePoolAbnormalNumber(pcContainerOverviewDTO.getNodePoolTotal());
            pcContainerOverviewDTO.setNodePoolOtherNumber(0);
        }else {
            try {
                ListNodesRequest body = new ListNodesRequest();
                FilterForListNodesInput filter = new FilterForListNodesInput();
                List<String> clusterIds = list.stream().map(SysCustomerContainerEntity::getClusterId).toList();
                filter.setIds(clusterIds);
                body.setFilter(filter);
                ListNodesResponse listNodesResponse = vkeApi.listNodes(body);
                List<ItemForListNodesOutput> items = listNodesResponse.getItems();
                int nodePoolNormalNumber = items.stream().filter(i -> i.getStatus().getPhase().equals("Running")).toList().size();
                pcContainerOverviewDTO.setNodePoolNormalNumber(nodePoolNormalNumber);
                int nodePoolAbnormalNumber = items.stream().filter(i -> i.getStatus().getPhase().equals("Failed")).toList().size();
                pcContainerOverviewDTO.setNodePoolAbnormalNumber(nodePoolAbnormalNumber);
                int nodePoolOtherNumber = pcContainerOverviewDTO.getNodePoolTotal() - nodePoolNormalNumber - nodePoolAbnormalNumber;
                pcContainerOverviewDTO.setNodePoolOtherNumber(nodePoolOtherNumber);
            }catch (ApiException e){
                log.error("获取节点失败,msg:",e);
                log.error("获取节点失败,msg:{}",e.getMessage());
            }
        }
        return pcContainerOverviewDTO;
    }

    @Override
    public Boolean deleteContainer(Long id) {
        SysCustomerContainerEntity containerEntity = sysCustomerContainerMapper.selectById(id);
        try {
            if ("Running".equals(containerEntity.getStatus().getVolcengineDesc())){
                VkeApi vkeApi = VoEngineApiClient.getVkeApi(CN_BEIJING.getId());
                DeleteClusterRequest body = new DeleteClusterRequest();
                body.setId(containerEntity.getClusterId());
                vkeApi.deleteCluster(body);
            }
            sysCustomerSubnetMapper.delete(Wrappers.lambdaQuery(SysCustomerSubnetEntity.class)
                    .eq(SysCustomerSubnetEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId()));
            sysCustomerSecurityGroupMapper.delete(Wrappers.lambdaQuery(SysCustomerSecurityGroupEntity.class)
                    .eq(SysCustomerSecurityGroupEntity::getCustomerInstanceId, containerEntity.getCustomerInstancesId()));
            sysCustomerInstancesMapper.delete(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getId, containerEntity.getCustomerInstancesId()));
            sysCustomerContainerMapper.deleteById(containerEntity.getId());
        } catch (Exception e) {
            log.info("删除容器失败,msg:{}",e.getMessage(),e);
            return false;
        }
        return true;
    }

    @Override
    public List<PcContainerListDTO> list(PcContainerListVO vo) {
        Long userId = vo.getCustomerId();
        List<SysCustomerContainerEntity> list = sysCustomerContainerMapper.selectList(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getCustomerId, userId)
                .eq(ObjectUtils.isNotEmpty(vo.getStatus()),SysCustomerContainerEntity::getStatus, vo.getStatus())
                .like(ObjectUtils.isNotEmpty(vo.getClusterName()), SysCustomerContainerEntity::getClusterName, vo.getClusterName()));
        if (ObjectUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        List<PcContainerListDTO> pcContainerList = new ArrayList<>();
        for (SysCustomerContainerEntity sysCustomerContainerEntity : list) {
            PcContainerListDTO pcContainerListDTO = new PcContainerListDTO();
            BeanUtils.copyProperties(sysCustomerContainerEntity,pcContainerListDTO);
            pcContainerListDTO.setStatus(sysCustomerContainerEntity.getStatus().getDesc());
            pcContainerListDTO.setKubernetesVersion(sysCustomerContainerEntity.getKubernetesVersion());
            pcContainerListDTO.setVciNumber(sysCustomerContainerEntity.getNodePoolNumber());
            SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getId, sysCustomerContainerEntity.getCustomerInstancesId()));
            pcContainerListDTO.setCpuNumber(sysCustomerInstancesEntity.getCpuNumber());
            pcContainerListDTO.setMemorySize(sysCustomerInstancesEntity.getMemorySize());
            if (sysCustomerContainerEntity.getStatus().equals(ContainerStatusEnum.RUNNING)){
                try {
                    VkeApi vkeApi = VoEngineApiClient.getVkeApi(CN_BEIJING.getId());
                    ListClustersRequest body = new ListClustersRequest();
                    FilterForListClustersInput filter = new FilterForListClustersInput();
                    filter.setIds(List.of(sysCustomerContainerEntity.getClusterId()));
                    body.setFilter(filter);
                    ListClustersResponse listClustersResponse = vkeApi.listClusters(body);
                    if (ObjectUtils.isNotEmpty(listClustersResponse.getItems())){
                        ItemForListClustersOutput itemForListClustersOutput = listClustersResponse.getItems().get(0);
                        pcContainerListDTO.setPrivateAccess(itemForListClustersOutput.getClusterConfig().getApiServerEndpoints().getPrivateIp().getIpv4());
                        pcContainerListDTO.setApiServerPublicAccess(itemForListClustersOutput.getClusterConfig().getApiServerEndpoints().getPublicIp().getIpv4());
                    }
                }catch (ApiException e){
                    log.error("获取容器详情失败,msg:{}",e.getMessage());
                }
                Map<String, InstanceForDescribeInstancesOutput> voEcsMap;

                EcsApi ecsApi = VoEngineApiClient.getEcsApi(sysCustomerInstancesEntity.getRegion() == null ? SourceRegionsEnum.CN_BEIJING.getId() : sysCustomerInstancesEntity.getRegion().getId());

                // 调用 火山实列接口，获取数据
                DescribeInstancesRequest request = new DescribeInstancesRequest();
                request.setInstanceIds(List.of(sysCustomerInstancesEntity.getInstanceId()));
                request.setMaxResults(2);
                try {
                    voEcsMap = ecsApi.describeInstances(request).getInstances().stream().collect(Collectors.toMap(InstanceForDescribeInstancesOutput::getInstanceId, i -> i));
                } catch (Exception e) {
                    log.error("获取火山服务器资源失败: ", e);
                    voEcsMap = new HashMap<>(0);
                }
                Map<String, InstanceForDescribeInstancesOutput> finalVoEcsMap = voEcsMap;
                InstanceForDescribeInstancesOutput output = finalVoEcsMap.get(sysCustomerInstancesEntity.getInstanceId());
                if (output != null) {

                    if (!sysCustomerInstancesEntity.getChargeType().equals(SourceChargeTypeEnum.POSTPAID_BY_HOUR)) {
                        Optional.of(output.getExpiredAt())
                                .ifPresent(expiredAt -> pcContainerListDTO.setExpiredTime(DateUtils.toDate(expiredAt, "yyyy-MM-dd'T'HH:mm:ssXXX")));
                    }
                }
            }
            pcContainerList.add(pcContainerListDTO);
        }
        return pcContainerList;
    }

    @Override
    public PcContainerDetailsDTO details(Long id) {
        SysCustomerContainerEntity containerEntity = sysCustomerContainerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getId, id));
        if (ObjectUtils.isEmpty(containerEntity)){
            return new PcContainerDetailsDTO();
        }
        PcContainerDetailsDTO pcContainerDetailsDTO = new PcContainerDetailsDTO();
        try {
            VkeApi vkeApi = VoEngineApiClient.getVkeApi(CN_BEIJING.getId());
            ListClustersRequest body = new ListClustersRequest();
            FilterForListClustersInput filter = new FilterForListClustersInput();
            filter.setIds(List.of(containerEntity.getClusterId()));
            body.setFilter(filter);
            ListClustersResponse listClustersResponse = vkeApi.listClusters(body);
            if (ObjectUtils.isNotEmpty(listClustersResponse.getItems())){
                ItemForListClustersOutput itemForListClustersOutput = listClustersResponse.getItems().get(0);
                pcContainerDetailsDTO.setKubernetesVersion(containerEntity.getKubernetesVersion());
                pcContainerDetailsDTO.setClusterId(itemForListClustersOutput.getId());
                pcContainerDetailsDTO.setClusterName(itemForListClustersOutput.getName());
                pcContainerDetailsDTO.setStatus(Objects.requireNonNull(getDescByVolcengineDesc(itemForListClustersOutput.getStatus().getPhase())).getDesc());
                pcContainerDetailsDTO.setPodSubnet(itemForListClustersOutput.getClusterConfig().getSubnetIds().get(0));
                pcContainerDetailsDTO.setNodeSecurityGroup(itemForListClustersOutput.getId() + "-common");
                pcContainerDetailsDTO.setPodSecurityGroup(itemForListClustersOutput.getId() + "-pod");
                pcContainerDetailsDTO.setVpcType("CXY");
                pcContainerDetailsDTO.setNetworkModel("VPC-CN | 共享弹性网卡");
                pcContainerDetailsDTO.setProxyMode("eBPF");
                pcContainerDetailsDTO.setPodSubnet(itemForListClustersOutput.getPodsConfig().getVpcCniConfig().getSubnetIds().get(0));
                pcContainerDetailsDTO.setServiceCidrV4(itemForListClustersOutput.getServicesConfig().getServiceCidrsv4().get(0));
                pcContainerDetailsDTO.setControlPlaneSubnet(itemForListClustersOutput.getPodsConfig().getVpcCniConfig().getSubnetIds().get(0));
                pcContainerDetailsDTO.setPrivateAccess(itemForListClustersOutput.getClusterConfig().getApiServerEndpoints().getPrivateIp().getIpv4());
                pcContainerDetailsDTO.setApiServerPublicAccess(itemForListClustersOutput.getClusterConfig().getApiServerEndpoints().getPublicIp().getIpv4());
                pcContainerDetailsDTO.setLoadBalancer(itemForListClustersOutput.getId() + "-apiserver-lb-internal");
                SysCustomerInstancesEntity sysCustomerInstancesEntity = sysCustomerInstancesMapper.selectOne(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                        .eq(SysCustomerInstancesEntity::getId, containerEntity.getCustomerInstancesId()));
                pcContainerDetailsDTO.setCpuCore(sysCustomerInstancesEntity.getCpuNumber());
                pcContainerDetailsDTO.setMemoryGiB(sysCustomerInstancesEntity.getMemorySize());
                pcContainerDetailsDTO.setInstanceNumber(containerEntity.getNodePoolNumber());
                // 处理私有网络
                SysCustomerVpcEntity vpcEntity = sysCustomerVpcMapper.selectOne(Wrappers.lambdaQuery(SysCustomerVpcEntity.class)
                        .eq(SysCustomerVpcEntity::getCustomerId, SecurityContext.getUserInfo().getUserId())
                        .eq(SysCustomerVpcEntity::getRegionId, CN_BEIJING));
                if (ObjectUtils.isNotEmpty(vpcEntity)){
                    pcContainerDetailsDTO.setNatName(vpcEntity.getVpcId() + "-natgateway");
                }
            }
        }catch (ApiException e){
            log.error("获取容器详情失败,msg:{}",e.getMessage());
            return pcContainerDetailsDTO;
        }

        return pcContainerDetailsDTO;
    }

    @Override
    public List<SysImageEntity> getImageVersion(PcContainerImageVO vo) {
        return sysImageMapper.selectList(Wrappers.lambdaQuery(SysImageEntity.class)
                        .eq(SysImageEntity::getType, vo.getType()))
                .stream()
                .filter(sysImageEntity -> {
                    String supportedSpecifications = sysImageEntity.getSupportedSpecifications();
                    List<String> scales = Arrays.asList(supportedSpecifications.split(","));
                    String instanceTypeId = vo.getInstanceTypeId();
                    String substring = instanceTypeId.substring(0, instanceTypeId.lastIndexOf('.'));
                    return scales.contains(substring) || scales.contains("all");
                })
                .collect(Collectors.toList());
    }
}
