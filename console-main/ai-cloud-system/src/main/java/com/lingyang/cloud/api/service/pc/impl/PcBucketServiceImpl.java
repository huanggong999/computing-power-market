package com.lingyang.cloud.api.service.pc.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcBucketDetailDTO;
import com.lingyang.cloud.api.model.query.PcBucketListQuery;
import com.lingyang.cloud.api.model.vo.PcBucketCreateVO;
import com.lingyang.cloud.api.service.pc.PcBucketService;
import com.lingyang.cloud.entity.SysCustomerBucketEntity;
import com.lingyang.cloud.entity.SysFile;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysCustomerBucketMapper;
import com.lingyang.cloud.mapper.SysFileMapper;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.security.SecurityContext;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.comm.common.ACLType;
import com.volcengine.tos.comm.common.AzRedundancyType;
import com.volcengine.tos.comm.common.StorageClassType;
import com.volcengine.tos.model.RequestInfo;
import com.volcengine.tos.model.bucket.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

import static com.volcengine.tos.comm.common.VersioningStatusType.VERSIONING_STATUS_ENABLED;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/12 11:31
 */
@Service
@Slf4j
public class PcBucketServiceImpl implements PcBucketService {

    @Resource
    private SysCustomerBucketMapper sysCustomerBucketMapper;

    @Resource
    private SysFileMapper sysFileMapper;

    @Resource
    private VolEngineConfig volEngineConfig;

    @Override
    public PageResult<SysCustomerBucketEntity> list(PageQuery<PcBucketListQuery> pageQuery) {
        PcBucketListQuery query = pageQuery.getQuery();
        pageQuery.startPage();
        Long userId = SecurityContext.getUserInfo().getUserId();
        List<SysCustomerBucketEntity> sysCustomerBucketEntities = sysCustomerBucketMapper.selectList(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                .eq(SysCustomerBucketEntity::getCustomerId, userId)
                .like(ObjectUtils.isNotEmpty(query.getBucketName()), SysCustomerBucketEntity::getName, query.getBucketName()));
        sysCustomerBucketEntities.forEach(item -> {
            try {
                TOSV2 tosv2 = VoEngineApiClient.getTosv2();
                ListBucketsV2Input input = new ListBucketsV2Input();
                ListBucketsV2Output listBucketsV2Output = tosv2.listBuckets(input);
                List<ListedBucket> buckets = listBucketsV2Output.getBuckets();
                buckets.stream().filter(i -> i.getName().equals(item.getName())).findFirst().ifPresent(listedBucket -> item.setRegion(Objects.requireNonNull(SourceRegionsEnum.getById(listedBucket.getLocation())).getName()));
            }catch (Exception e){
                log.error("火山云获取bucket区域失败：{}", e.getMessage(),e);
            }
        });
        return PageResult.of(sysCustomerBucketEntities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean create(PcBucketCreateVO vo) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        try {
            TOSV2 tosv2 = VoEngineApiClient.getTosv2();
            CreateBucketV2Input var1 = new CreateBucketV2Input();
            var1.setBucket(vo.getName());
            switch (vo.getBucketStrategy()){
                case 0 : var1.setAcl(ACLType.ACL_PRIVATE);break;
                case 1 : var1.setAcl(ACLType.ACL_PUBLIC_READ);break;
                case 2 : var1.setAcl(ACLType.ACL_PUBLIC_READ_WRITE);break;
            }
            var1.setStorageClass(StorageClassType.STORAGE_CLASS_STANDARD);//默认为标准存储
            if (vo.getRedundancyType() == 0){
                var1.setAzRedundancy(AzRedundancyType.AZ_REDUNDANCY_SINGLE_AZ);
            }else if (vo.getRedundancyType() == 1){
                var1.setAzRedundancy(AzRedundancyType.AZ_REDUNDANCY_MULTI_AZ);
            }
            var1.setProjectName(volEngineConfig.getProjectName());
            CreateBucketV2Output bucket = tosv2.createBucket(var1);
            RequestInfo requestInfo = bucket.getRequestInfo();
            log.info("创建bucket成功");
            if (ObjectUtils.isNotEmpty(requestInfo) && requestInfo.getStatusCode() == 200){
                SysCustomerBucketEntity entity = new SysCustomerBucketEntity();
                entity.setName(vo.getName());
                entity.setCustomerId(userId);
                entity.setBucketStrategy(vo.getBucketStrategy());
                entity.setStorageType("标准存储");
                entity.setRedundancyType(vo.getRedundancyType());
                if (vo.getIsVersion()){
                    entity.setIsVersion(1);
                }else {
                    entity.setIsVersion(0);
                }
                sysCustomerBucketMapper.insert(entity);
            }
            // 开启版本控制
            if (vo.getIsVersion()){
                PutBucketVersioningInput putBucketVersioningInput = new PutBucketVersioningInput();
                putBucketVersioningInput.setBucket(vo.getName());
                putBucketVersioningInput.setStatus(VERSIONING_STATUS_ENABLED);
                tosv2.putBucketVersioning(putBucketVersioningInput);
                log.info("开启版本控制成功");
            }
        }catch (Exception e){
            log.error("火山云创建bucket失败：{}", e.getMessage(),e);
            throw new HttpServiceException("火山云创建桶失败");
        }
        return true;
    }

    @Override
    public Boolean delete(String name) {
        try {
            TOSV2 tosv2 = VoEngineApiClient.getTosv2();
            tosv2.deleteBucket(name);
            sysCustomerBucketMapper.delete(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                    .eq(SysCustomerBucketEntity::getName, name));
            log.info("删除bucket成功");
        }catch (Exception e){
            log.error("火山云删除bucket失败：{}", e.getMessage(),e);
            throw new HttpServiceException("火山云删除桶失败");
        }
        return true;
    }

    @Override
    public PcBucketDetailDTO detail(String name) {
        SysCustomerBucketEntity sysCustomerBucketEntity = sysCustomerBucketMapper.selectOne(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                .eq(SysCustomerBucketEntity::getName, name));
        PcBucketDetailDTO pcBucketDetailDTO = BeanUtil.copyProperties(sysCustomerBucketEntity, PcBucketDetailDTO.class);
        List<SysFile> list = sysFileMapper.selectList(Wrappers.lambdaQuery(SysFile.class)
                .eq(SysFile::getBucketId, sysCustomerBucketEntity.getId())
                .eq(SysFile::getType,2)
                .eq(SysFile::getDelFlag, 0));
        BigDecimal totalSize = list.stream()
                .map(SysFile::getSize)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 将MB转换为GB，并保留4位小数
        totalSize = totalSize.divide(new BigDecimal(1024), 4, RoundingMode.HALF_UP);
        pcBucketDetailDTO.setBucketObjectSize(totalSize);
        pcBucketDetailDTO.setBucketObjectCount(list.size());
        try {
            TOSV2 tosv2 = VoEngineApiClient.getTosv2();
            ListBucketsV2Input input = new ListBucketsV2Input();
            ListBucketsV2Output listBucketsV2Output = tosv2.listBuckets(input);
            List<ListedBucket> buckets = listBucketsV2Output.getBuckets();
            buckets.stream().filter(i -> i.getName().equals(name)).findFirst().ifPresent(listedBucket -> {
                pcBucketDetailDTO.setRegion(Objects.requireNonNull(SourceRegionsEnum.getById(listedBucket.getLocation())).getName());
                pcBucketDetailDTO.setExtranetEndpoint(listedBucket.getExtranetEndpoint());
                pcBucketDetailDTO.setIntranetEndpoint(listedBucket.getIntranetEndpoint());
                String extranetS3Endpoint = listedBucket.getExtranetEndpoint().replace("tos-", "tos-s3-");
                pcBucketDetailDTO.setExtranetS3Endpoint(extranetS3Endpoint);
                String intranetS3Endpoint = listedBucket.getIntranetEndpoint().replace("tos-", "tos-s3-");
                pcBucketDetailDTO.setIntranetS3Endpoint(intranetS3Endpoint);
                pcBucketDetailDTO.setExtranetDomain(name + "." + listedBucket.getExtranetEndpoint());
                pcBucketDetailDTO.setIntranetDomain(name + "." + listedBucket.getIntranetEndpoint());
            });

        }catch (Exception e){
            log.error("火山云获取bucket详情失败：{}", e.getMessage(),e);
        }
        return pcBucketDetailDTO;
    }

    @Override
    public Boolean check(String name) {
        SysCustomerBucketEntity sysCustomerBucketEntity = sysCustomerBucketMapper.selectOne(Wrappers.lambdaQuery(SysCustomerBucketEntity.class)
                .eq(SysCustomerBucketEntity::getName, name));
        return ObjectUtils.isNotEmpty(sysCustomerBucketEntity);
    }
}
