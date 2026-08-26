package com.lingyang.cloud.api.service.pc.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.api.model.dto.PcImageOciOverviewDTO;
import com.lingyang.cloud.api.model.dto.PcImageOciOverviewPageDTO;
import com.lingyang.cloud.api.model.dto.PcImageRepositoryDTO;
import com.lingyang.cloud.api.model.query.PcImageOciOverviewQuery;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryDeleteVO;
import com.lingyang.cloud.api.model.vo.PcImageRepositoryVO;
import com.lingyang.cloud.api.service.pc.PcImageRepositoryService;
import com.lingyang.cloud.entity.SysCustomerImageRepositoryEntity;
import com.lingyang.cloud.enums.source.SourceRegionsEnum;
import com.lingyang.cloud.mapper.SysImageRepositoryMapper;
import com.lingyang.cloud.voengine.client.VoEngineApiClient;
import com.lingyang.cloud.voengine.config.VolEngineConfig;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.volcengine.ApiException;
import com.volcengine.cr.CrApi;
import com.volcengine.cr.model.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/17 15:12
 */
@Service
@Slf4j
public class PcImageRepositoryServiceImpl implements PcImageRepositoryService {

    @Resource
    private SysImageRepositoryMapper sysImageRepositoryMapper;

    @Resource
    private VolEngineConfig volEngineConfig;
    @Override
    public List<SysCustomerImageRepositoryEntity> list(PcImageRepositoryVO vo) {
        Long userId = vo.getCustomerId();
        return sysImageRepositoryMapper.selectList(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getCustomerId, userId)
                .eq(ObjectUtils.isNotEmpty(vo.getStatus()),SysCustomerImageRepositoryEntity::getStatus, vo.getStatus())
                .like(ObjectUtils.isNotEmpty(vo.getInstanceName()), SysCustomerImageRepositoryEntity::getInstanceName, vo.getInstanceName()));
    }

    @Override
    public PcImageRepositoryDTO detail(String instanceName) {
        Long userId = SecurityContext.getUserInfo().getUserId();
        SysCustomerImageRepositoryEntity sysCustomerImageRepositoryEntity = sysImageRepositoryMapper.selectOne(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getCustomerId, userId)
                .eq(SysCustomerImageRepositoryEntity::getInstanceName, instanceName));
        PcImageRepositoryDTO pcImageRepositoryDTO = BeanUtil.copyProperties(sysCustomerImageRepositoryEntity, PcImageRepositoryDTO.class);
        pcImageRepositoryDTO.setRegion(SourceRegionsEnum.CN_BEIJING.getName());
        pcImageRepositoryDTO.setVersion("标准版");
        pcImageRepositoryDTO.setProject(volEngineConfig.getProjectName());
        try {
            CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
            GetUserRequest body = new GetUserRequest();
            body.setRegistry(instanceName);
            log.info("获取用户信息传参{}",body);
            GetUserResponse user = crApi.getUser(body);
            log.info("获取用户信息{}",user);
            if (ObjectUtils.isNotEmpty(user)){
                pcImageRepositoryDTO.setUsername(user.getUsername());
            }
        }catch (Exception e){
            log.error("获取用户信息失败{}",e.getMessage(),e);
        }
        return pcImageRepositoryDTO;
    }

    @Override
    public Boolean setPassword(PcImageRepositoryVO vo) {
        boolean tag = false;
        try {
            CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
            SetUserRequest body = new SetUserRequest();
            body.setRegistry(vo.getInstanceName());
            body.setPassword(vo.getPassword());
            crApi.setUser(body);
            sysImageRepositoryMapper.update(null,Wrappers.lambdaUpdate(SysCustomerImageRepositoryEntity.class)
                    .eq(SysCustomerImageRepositoryEntity::getInstanceName,vo.getInstanceName())
                    .set(SysCustomerImageRepositoryEntity::getPassword,vo.getPassword()));
            tag = true;
        }catch (Exception e){
            log.error("设置密码失败:{}",e.getMessage(),e);
        }
        return tag;
    }

    @Override
    public PcImageOciOverviewDTO ociOverview(String instanceName) {
        SysCustomerImageRepositoryEntity sysCustomerImageRepositoryEntity = sysImageRepositoryMapper.selectOne(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getInstanceName, instanceName));
        if (ObjectUtils.isNotEmpty(sysCustomerImageRepositoryEntity)){
            PcImageOciOverviewDTO pcImageOciOverviewDTO = BeanUtil.copyProperties(sysCustomerImageRepositoryEntity, PcImageOciOverviewDTO.class);
            pcImageOciOverviewDTO.setType("共有");
            return pcImageOciOverviewDTO;
        }
        return new PcImageOciOverviewDTO();
    }

    @Override
    public Result<List<PcImageOciOverviewPageDTO>> page(PcImageOciOverviewQuery pageQuery) {
        log.info("pageQuery{}",pageQuery);
        SysCustomerImageRepositoryEntity sysCustomerImageRepositoryEntity = sysImageRepositoryMapper.selectOne(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getInstanceName, pageQuery.getInstanceName()));
        List<PcImageOciOverviewPageDTO> pcImageOciOverviewPageDTOS = new ArrayList<>();
        try{
            CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
            ListTagsRequest body = new ListTagsRequest();
            body.setRegistry(pageQuery.getInstanceName());
            body.setNamespace(sysCustomerImageRepositoryEntity.getNamespace());
            body.setRepository(sysCustomerImageRepositoryEntity.getOciName());
            body.setPageNumber(pageQuery.getPageNo());
            body.setPageSize(pageQuery.getPageSize());
            FilterForListTagsInput filterForListTagsInput = new FilterForListTagsInput();
            filterForListTagsInput.setTypes(List.of(pageQuery.getType()));
            body.setFilter(filterForListTagsInput);
            log.info("获取镜像列表传参{}",body);
            ListTagsResponse listTagsResponse = crApi.listTags(body);
            log.info("获取镜像列表返回{}",listTagsResponse);
            List<ItemForListTagsOutput> items = listTagsResponse.getItems();
            if (ObjectUtils.isNotEmpty(items)){
                for (ItemForListTagsOutput item : items) {
                    PcImageOciOverviewPageDTO pcImageOciOverviewPageDTO = new PcImageOciOverviewPageDTO();
                    pcImageOciOverviewPageDTO.setImageVersion(item.getName());
                    pcImageOciOverviewPageDTO.setType(item.getType());
                    if ("Image".equals(item.getType())) {
                        ImageAttributeForListTagsOutput imageAttributeForListTagsOutput = item.getImageAttributes().get(0);
                        pcImageOciOverviewPageDTO.setImageSize(imageAttributeForListTagsOutput.getSize());
                        pcImageOciOverviewPageDTO.setImageDigest(imageAttributeForListTagsOutput.getDigest());
                        pcImageOciOverviewPageDTO.setOsArch(imageAttributeForListTagsOutput.getOs() + "/" + imageAttributeForListTagsOutput.getArchitecture());
                    }else if ("Chart".equals(item.getType())) {
                        ChartAttributeForListTagsOutput chartAttribute = item.getChartAttribute();
                        pcImageOciOverviewPageDTO.setArtifactVersion(chartAttribute.getVersion());
                    }
                    String domain = sysCustomerImageRepositoryEntity.getDomain();
                    String namespace = sysCustomerImageRepositoryEntity.getNamespace();
                    String ociName = sysCustomerImageRepositoryEntity.getOciName();
                    pcImageOciOverviewPageDTO.setNetworkAddress(domain + "/" + namespace + "/" + ociName + ":" + item.getName());
                    pcImageOciOverviewPageDTO.setUpdateTime(item.getPushTime());
                    pcImageOciOverviewPageDTOS.add(pcImageOciOverviewPageDTO);
                }
            }
        }catch (Exception e) {
            log.error("获取镜像版本列表失败,msg:{}", e.getMessage(),e);
            throw new HttpServiceException("获取失败");
        }

        return Result.success(pcImageOciOverviewPageDTOS);
    }

    @Override
    public String getUsername() {
        List<SysCustomerImageRepositoryEntity> customerImageRepositoryEntities = sysImageRepositoryMapper.selectList(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getCustomerId, SecurityContext.getUserInfo().getUserId()));
        if (ObjectUtils.isNotEmpty(customerImageRepositoryEntities)){
            try {
                CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
                GetUserRequest body = new GetUserRequest();
                body.setRegistry(customerImageRepositoryEntities.get(0).getInstanceName());
                GetUserResponse user = crApi.getUser(body);
                if (ObjectUtils.isNotEmpty(user)){
                    return user.getUsername();
                }
            }catch (ApiException e){
                log.error("获取用户失败,msg:{}",e.getMessage());
            }
        }
        return "*******";
    }

    @Override
    public DeleteTagsResponse deleteImage(PcImageRepositoryDeleteVO vo) {
        log.info("deleteImage入参{}",vo);
        DeleteTagsResponse deleteTagsResponse = new DeleteTagsResponse();
        try {
            CrApi crApi = VoEngineApiClient.getCrApi(SourceRegionsEnum.CN_BEIJING.getId());
            DeleteTagsRequest body = new DeleteTagsRequest();
            body.setRegistry(vo.getInstanceName());
            body.setNamespace(vo.getNamespace());
            body.setRepository(vo.getOciName());
            body.setNames(vo.getImageVersions());
            deleteTagsResponse = crApi.deleteTags(body);
        }catch (Exception e){
            log.error("删除镜像失败,msg:{}",e.getMessage(),e);
        }
        return deleteTagsResponse;
    }

    @Override
    public Boolean imageNameExist(String imageName) {
        SysCustomerImageRepositoryEntity customerImageRepositoryEntities = sysImageRepositoryMapper.selectOne(Wrappers.lambdaQuery(SysCustomerImageRepositoryEntity.class)
                .eq(SysCustomerImageRepositoryEntity::getInstanceName,imageName));
        return ObjectUtils.isNotEmpty(customerImageRepositoryEntities);
    }
}
