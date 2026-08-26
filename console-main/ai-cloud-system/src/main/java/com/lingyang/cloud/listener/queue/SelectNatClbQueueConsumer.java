package com.lingyang.cloud.listener.queue;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.common.constant.CacheQueueConstant;
import com.lingyang.cloud.entity.SysCustomerContainerEntity;
import com.lingyang.cloud.entity.SysCustomerVpcEntity;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysCustomerContainerMapper;
import com.lingyang.cloud.mapper.SysCustomerVpcMapper;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.cache.queue.CacheQueueConsumer;
import com.volcengine.clb.ClbApi;
import com.volcengine.clb.model.DescribeLoadBalancersRequest;
import com.volcengine.clb.model.DescribeLoadBalancersResponse;
import com.volcengine.clb.model.LoadBalancerForDescribeLoadBalancersOutput;
import com.volcengine.natgateway.NatgatewayApi;
import com.volcengine.natgateway.model.DescribeNatGatewaysRequest;
import com.volcengine.natgateway.model.DescribeNatGatewaysResponse;
import com.volcengine.natgateway.model.NatGatewayForDescribeNatGatewaysOutput;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.lingyang.cloud.enums.source.SourceRegionsEnum.CN_BEIJING;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/24 11:56
 */
@Service
@Slf4j
public class SelectNatClbQueueConsumer implements CacheQueueConsumer {
    @Resource
    private SysCustomerContainerMapper sysCustomerContainerMapper;
    @Resource
    private SysCustomerVpcMapper sysCustomerVpcMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invoke(String messageContent) throws Exception {
        log.info("查询natId和clbId开始-----------");
        SysCustomerContainerEntity sysCustomerContainerEntity = sysCustomerContainerMapper.selectOne(Wrappers.lambdaQuery(SysCustomerContainerEntity.class)
                .eq(SysCustomerContainerEntity::getClusterId, messageContent));
        SysCustomerVpcEntity vpcEntity = sysCustomerVpcMapper.selectOne(Wrappers.lambdaQuery(SysCustomerVpcEntity.class)
                .eq(SysCustomerVpcEntity::getCustomerId, sysCustomerContainerEntity.getCustomerId())
                .eq(SysCustomerVpcEntity::getRegionId, CN_BEIJING));
        if (ObjectUtils.isNotEmpty(vpcEntity) && ObjectUtils.isNotEmpty(sysCustomerContainerEntity)){
            NatgatewayApi natgatewayApi = VoEngineApiClient.getNatgatewayApi(SourceRegionsEnum.CN_BEIJING.getId());
            DescribeNatGatewaysRequest body = new DescribeNatGatewaysRequest();
            String vpcId = vpcEntity.getVpcId();
            body.setNatGatewayName(vpcId + "-natgateway");
            DescribeNatGatewaysResponse describeNatGatewaysResponse = natgatewayApi.describeNatGateways(body);
            List<NatGatewayForDescribeNatGatewaysOutput> natGateways = describeNatGatewaysResponse.getNatGateways();
            log.info("查询到natGateways:{}", JSON.toJSONString(natGateways));
            if (ObjectUtils.isNotEmpty(natGateways)){
                String natGatewayId = natGateways.get(0).getNatGatewayId();
                sysCustomerContainerEntity.setNatId(natGatewayId);
            }
            ClbApi clbApi = VoEngineApiClient.getClbApi(CN_BEIJING.getId());
            DescribeLoadBalancersRequest body2 = new DescribeLoadBalancersRequest();
            body2.setLoadBalancerName(messageContent + "-apiserver-lb-internal");
            DescribeLoadBalancersResponse describeLoadBalancersResponse = clbApi.describeLoadBalancers(body2);
            List<LoadBalancerForDescribeLoadBalancersOutput> loadBalancers = describeLoadBalancersResponse.getLoadBalancers();
            log.info("查询到clb:{}", JSON.toJSONString(loadBalancers));
            if (ObjectUtils.isNotEmpty(loadBalancers)){
                String loadBalancerId = loadBalancers.get(0).getLoadBalancerId();
                sysCustomerContainerEntity.setClbId(loadBalancerId);
            }
            sysCustomerContainerMapper.updateById(sysCustomerContainerEntity);
        }
    }

    @Override
    public String messageType() {
        return CacheQueueConstant.NAT_GATEWAY_ID_QUEUE_TYPE;
    }
}
