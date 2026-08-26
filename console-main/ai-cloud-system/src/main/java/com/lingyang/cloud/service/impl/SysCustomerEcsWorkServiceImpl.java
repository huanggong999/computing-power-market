package com.lingyang.cloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.mapper.*;
import com.lingyang.cloud.model.dto.SysCustomerEcsWorkEipDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkDetailDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkInstancesDTO;
import com.lingyang.cloud.model.dto.SysEcsWorkPageDTO;
import com.lingyang.cloud.model.query.customer.SysCustomerEcsWorkQuery;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkOperationVO;
import com.lingyang.cloud.model.vo.customer.SysCustomerEcsWorkVO;
import com.lingyang.cloud.model.vo.regions.SourceRegionZoneVO;
import com.lingyang.cloud.service.SysCustomerEcsWorkService;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.volcengine.ApiException;
import com.volcengine.ecs.EcsApi;
import com.volcengine.ecs.model.DescribeImagesRequest;
import com.volcengine.ecs.model.DescribeImagesResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.lingyang.cloud.enums.source.SourceTypeEnum.ECS;

/**
 * @author Administrator
 */
@Service
@Slf4j
public class SysCustomerEcsWorkServiceImpl implements SysCustomerEcsWorkService {

    @Resource
    private SysCustomerEcsWorkMapper sysCustomerEcsWorkMapper;

    @Resource
    private SysCustomerVolumeMapper sysCustomerVolumeMapper;

    @Resource
    private SysOrderSourceMapper sysOrderSourceMapper;

    @Resource
    private SysCustomerEcsWorkEipMapper sysCustomerEcsWorkEipMapper;

    @Resource
    private SysCustomerInstancesMapper sysCustomerInstancesMapper;

    @Override
    public PageResult<SysEcsWorkPageDTO> getPage(PageQuery<SysCustomerEcsWorkQuery> build) {
        SysCustomerEcsWorkQuery query = build.getQuery();
        build.startPage();
        List<SysEcsWorkPageDTO> list = sysCustomerEcsWorkMapper.getList(query);
        if (CollectionUtils.isNotEmpty(list)){
            list.forEach(item -> {
                List<SysCustomerEcsWorkEipEntity> sysCustomerEcsWorkEipEntities = sysCustomerEcsWorkEipMapper.selectList(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                        .eq(SysCustomerEcsWorkEipEntity::getEcsWorkId, item.getId()));
                List<SysEcsWorkInstancesDTO> sysEcsWorkInstancesList = BeanUtil.copyToList(sysCustomerEcsWorkEipEntities, SysEcsWorkInstancesDTO.class);
                item.setInstanceStatusList(sysEcsWorkInstancesList);

                List<SysCustomerVolumeEntity> volumeEntities = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                        .eq(SysCustomerVolumeEntity::getOrderId, item.getOrderId()));
                for (SysCustomerVolumeEntity volumeEntity : volumeEntities) {
                    if ("system".equals(volumeEntity.getKind())) {
                        item.setSystemDisk(volumeEntity.getVolumeType());
                        item.setSystemDiskSize(volumeEntity.getSize());
                    } else {
                        item.setDataDisk(volumeEntity.getVolumeType());
                        item.setDataDiskSize(volumeEntity.getSize());
                    }
                }
                List<SysOrderSourceEntity> sysOrderSourceEntityList = sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                        .eq(SysOrderSourceEntity::getOrderId, item.getOrderId())
                        .eq(SysOrderSourceEntity::getSourceType, ECS));
                SysOrderSourceEntity sysOrderSourceEntity = sysOrderSourceEntityList.get(0);
                item.setProductName(sysOrderSourceEntity.getProductName());
                item.setProductType(sysOrderSourceEntity.getProductType());
                item.setChargeType(sysOrderSourceEntity.getChargeType());
                item.setDuration(sysOrderSourceEntity.getDuration());
                item.setDurationUnit(sysOrderSourceEntity.getDurationUnit());
                item.setLoginName("root");
                item.setLoginPassword(sysOrderSourceEntity.getConfigDetail().getString("password"));
                try {
                    DescribeImagesResponse describeImagesResponse = describeImages(item.getImageId());
                    if (ObjectUtils.isNotEmpty(describeImagesResponse)){
                        describeImagesResponse.getImages().forEach(image -> {
                            item.setOs(image.getImageName());
                            item.setImageBrand(image.getPlatform());
                            item.setImageType(image.getVisibility());
                        });
                    }
                }catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return PageResult.of(list);
    }

    @Override
    public SysEcsWorkDetailDTO getDetail(Long id) {
        SysEcsWorkDetailDTO sysEcsWorkPageDTO = sysCustomerEcsWorkMapper.getDetail(id);
        if (ObjectUtils.isNotEmpty(sysEcsWorkPageDTO)){
            List<SysCustomerEcsWorkEipEntity> sysCustomerEcsWorkEipEntities = sysCustomerEcsWorkEipMapper.selectList(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                    .eq(SysCustomerEcsWorkEipEntity::getEcsWorkId, id));
            List<SysEcsWorkInstancesDTO> sysEcsWorkInstancesList = BeanUtil.copyToList(sysCustomerEcsWorkEipEntities, SysEcsWorkInstancesDTO.class);
            sysEcsWorkPageDTO.setInstanceStatusList(sysEcsWorkInstancesList);

            List<SysCustomerVolumeEntity> volumeEntities = sysCustomerVolumeMapper.selectList(Wrappers.lambdaQuery(SysCustomerVolumeEntity.class)
                    .eq(SysCustomerVolumeEntity::getOrderId, sysEcsWorkPageDTO.getOrderId()));
            if (CollectionUtils.isNotEmpty(volumeEntities)){
                for (SysCustomerVolumeEntity volumeEntity : volumeEntities) {
                    if ("system".equals(volumeEntity.getKind())) {
                        sysEcsWorkPageDTO.setSystemDisk(volumeEntity.getVolumeType());
                        sysEcsWorkPageDTO.setSystemDiskSize(volumeEntity.getSize());
                    } else {
                        sysEcsWorkPageDTO.setDataDisk(volumeEntity.getVolumeType());
                        sysEcsWorkPageDTO.setDataDiskSize(volumeEntity.getSize());
                    }
                }
            }
            List<SysOrderSourceEntity> sysOrderSourceEntityList = sysOrderSourceMapper.selectList(Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                    .eq(SysOrderSourceEntity::getOrderId, sysEcsWorkPageDTO.getOrderId())
                    .eq(SysOrderSourceEntity::getSourceType, ECS));
            SysOrderSourceEntity sysOrderSourceEntity = sysOrderSourceEntityList.get(0);
            sysEcsWorkPageDTO.setProductName(sysOrderSourceEntity.getProductName());
            sysEcsWorkPageDTO.setProductType(sysOrderSourceEntity.getProductType());
            sysEcsWorkPageDTO.setChargeType(sysOrderSourceEntity.getChargeType());
            sysEcsWorkPageDTO.setDuration(sysOrderSourceEntity.getDuration());
            sysEcsWorkPageDTO.setDurationUnit(sysOrderSourceEntity.getDurationUnit());
            sysEcsWorkPageDTO.setLoginName("root");
            sysEcsWorkPageDTO.setLoginPassword(sysOrderSourceEntity.getConfigDetail().getString("password"));
            try {
                DescribeImagesResponse describeImagesResponse = describeImages(sysEcsWorkPageDTO.getImageId());
                if (ObjectUtils.isNotEmpty(describeImagesResponse)){
                    describeImagesResponse.getImages().forEach(image -> {
                        sysEcsWorkPageDTO.setOs(image.getImageName());
                        sysEcsWorkPageDTO.setImageBrand(image.getPlatform());
                        sysEcsWorkPageDTO.setImageType(image.getVisibility());
                    });
                }
            }catch (Exception e) {
                throw new RuntimeException(e);
            }

            List<SysCustomerInstancesEntity> sysCustomerInstancesEntityList = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getOrderId, sysEcsWorkPageDTO.getOrderId()));

            SourceRegionZoneVO zv = new SourceRegionZoneVO(sysCustomerInstancesEntityList.get(0).getZoneId());
            zv.setName(zv.getName());
            sysEcsWorkPageDTO.setZone(zv);
        }

        return sysEcsWorkPageDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean openWork(SysCustomerEcsWorkVO vo) {
        try {
            SysCustomerEcsWorkEntity sysCustomerEcsWorkEntity = sysCustomerEcsWorkMapper.selectById(vo.getId());
            List<SysCustomerInstancesEntity> sysCustomerInstancesEntities = sysCustomerInstancesMapper.selectList(Wrappers.lambdaQuery(SysCustomerInstancesEntity.class)
                    .eq(SysCustomerInstancesEntity::getOrderId, sysCustomerEcsWorkEntity.getOrderId()));

            List<SysCustomerEcsWorkEipDTO> publicInfos = vo.getPublicInfos();
            if (CollectionUtils.isNotEmpty(publicInfos)){
                List<SysCustomerEcsWorkEipEntity> sysCustomerEcsWorkEipEntities = BeanUtil.copyToList(publicInfos, SysCustomerEcsWorkEipEntity.class);
                int minSize = Math.min(sysCustomerEcsWorkEipEntities.size(), sysCustomerInstancesEntities.size());
                // 如果 sysCustomerEcsWorkEipEntities 比 sysCustomerInstancesEntities 多，后面的就不设置 instancesId
                // 如果 sysCustomerInstancesEntities 比 sysCustomerEcsWorkEipEntities 多，后面的 instanceId 就用不到
                for (int i = 0; i < minSize; i++) {
                    SysCustomerEcsWorkEipEntity eipEntity = sysCustomerEcsWorkEipEntities.get(i);
                    SysCustomerInstancesEntity instanceEntity = sysCustomerInstancesEntities.get(i);
                    eipEntity.setInstanceId(instanceEntity.getId());
                    eipEntity.setEcsWorkId(sysCustomerEcsWorkEntity.getId());
                    eipEntity.setOperationStatus(2);
                }
                sysCustomerEcsWorkEipMapper.batchInsert(sysCustomerEcsWorkEipEntities);
            }
        }catch (Exception e){
            log.error("工单开通失败:{}",e.getMessage(), e);
            throw new HttpServiceException("工单开通失败");
        }
        return true;
    }

    @Override
    public Boolean operation(SysCustomerEcsWorkOperationVO vo) {
        SysCustomerEcsWorkEipEntity sysCustomerEcsWorkEipEntity = sysCustomerEcsWorkEipMapper.selectOne(Wrappers.lambdaQuery(SysCustomerEcsWorkEipEntity.class)
                .eq(SysCustomerEcsWorkEipEntity::getInstanceId, vo.getInstanceId()));

        switch (vo.getOperationType()) {
            case 1: sysCustomerEcsWorkEipEntity.setOperationStatus(4);
                break;
                case 2: sysCustomerEcsWorkEipEntity.setOperationStatus(5);
                    break;
                    case 3: sysCustomerEcsWorkEipEntity.setOperationStatus(2);
                        break;
                        case 4: //  续费（暂不处理）
                            break;
        }
        return sysCustomerEcsWorkEipMapper.updateById(sysCustomerEcsWorkEipEntity)>0;
    }

    public DescribeImagesResponse describeImages(String imageId) throws Exception{
        EcsApi ecsApi = VoEngineApiClient.getEcsApi();
        try {
            DescribeImagesRequest request = new DescribeImagesRequest();
            request.setImageIds(Collections.singletonList(imageId));
            return ecsApi.describeImages(request);
        } catch (ApiException e) {
            return new DescribeImagesResponse();
        }
    }
}
