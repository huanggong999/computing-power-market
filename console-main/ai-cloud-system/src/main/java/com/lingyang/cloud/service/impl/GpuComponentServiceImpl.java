package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.entity.GpuComponentEntity;
import com.lingyang.cloud.mapper.GpuComponentMapper;
import com.lingyang.cloud.model.edit.gpu.GpuComponentEdit;
import com.lingyang.cloud.model.query.gpu.GpuComponentQuery;
import com.lingyang.cloud.model.vo.system.GpuComponentVO;
import com.lingyang.cloud.service.GpuComponentService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * GPU基础组件镜像服务实现
 */
@Service
public class GpuComponentServiceImpl implements GpuComponentService {

    @Resource
    private GpuComponentMapper gpuComponentMapper;

    @Override
    public PageResult<GpuComponentVO> getComponentPage(GpuComponentQuery query, PageQuery pageQuery) {
        pageQuery.startPage();
        String componentName = query == null ? null : StringUtils.defaultIfBlank(query.getComponentName(), query.getName());
        LambdaQueryWrapper<GpuComponentEntity> wrapper = Wrappers.lambdaQuery(GpuComponentEntity.class)
                .eq(GpuComponentEntity::getDelFlag, 0)
                .eq(query != null && query.getStatus() != null, GpuComponentEntity::getStatus, query == null ? null : query.getStatus())
                .like(StringUtils.isNotBlank(componentName), GpuComponentEntity::getComponentName, componentName)
                .orderByAsc(GpuComponentEntity::getSortOrder)
                .orderByAsc(GpuComponentEntity::getId);
        List<GpuComponentEntity> list = gpuComponentMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return PageResult.of(new ArrayList<>());
        }
        return PageResult.of(list.stream().map(entity -> {
            fillStructuredFields(entity);
            return toVO(entity);
        }).toList());
    }

    @Override
    public void saveOrUpdate(GpuComponentEdit edit) {
        validateImageAddress(edit);
        GpuComponentEntity entity = new GpuComponentEntity();
        BeanUtils.copyProperties(edit, entity);
        normalizeEntity(entity);
        if (entity.getId() == null) {
            entity.setStatus(entity.getStatus() == null ? 1 : entity.getStatus());
            entity.setSortOrder(entity.getSortOrder() == null ? 0 : entity.getSortOrder());
            gpuComponentMapper.insert(entity);
        } else {
            gpuComponentMapper.updateById(entity);
        }
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (id == null) {
            throw new HttpServiceException("组件ID不能为空");
        }
        if (!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw new HttpServiceException("组件状态无效");
        }
        GpuComponentEntity entity = new GpuComponentEntity();
        entity.setId(id);
        entity.setStatus(status);
        gpuComponentMapper.updateById(entity);
    }

    private void validateImageAddress(GpuComponentEdit edit) {
        if (edit == null) {
            throw new HttpServiceException("组件信息不能为空");
        }
        LambdaQueryWrapper<GpuComponentEntity> wrapper = Wrappers.lambdaQuery(GpuComponentEntity.class)
                .eq(GpuComponentEntity::getDelFlag, 0)
                .eq(GpuComponentEntity::getImageAddress, edit.getImageAddress())
                .ne(edit.getId() != null, GpuComponentEntity::getId, edit.getId())
                .last("limit 1");
        if (gpuComponentMapper.selectOne(wrapper) != null) {
            throw new HttpServiceException("镜像地址已存在，请重新输入");
        }
    }

    private GpuComponentVO toVO(GpuComponentEntity entity) {
        GpuComponentVO vo = new GpuComponentVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    private void normalizeEntity(GpuComponentEntity entity) {
        fillStructuredFields(entity);
        String baseImage = composeBaseImage(entity);
        if (StringUtils.isNotBlank(baseImage)) {
            entity.setBaseImage(baseImage);
        }
    }

    private void fillStructuredFields(GpuComponentEntity entity) {
        if (entity == null || StringUtils.isBlank(entity.getBaseImage())) {
            return;
        }
        List<String> tokens = Arrays.stream(entity.getBaseImage().split("\\+"))
                .map(StringUtils::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .toList();
        if (tokens.isEmpty()) {
            return;
        }
        if (StringUtils.isBlank(entity.getComponentVersion())) {
            entity.setComponentVersion(parseComponentVersion(entity.getComponentName(), tokens.get(0)));
        }
        for (String token : tokens) {
            String lowerToken = token.toLowerCase(Locale.ROOT);
            if (StringUtils.isBlank(entity.getPythonVersion()) && lowerToken.startsWith("python")) {
                entity.setPythonVersion(StringUtils.trimToNull(token.substring("python".length())));
                continue;
            }
            if ((StringUtils.isBlank(entity.getOsName()) || StringUtils.isBlank(entity.getOsVersion())) && lowerToken.startsWith("ubuntu")) {
                entity.setOsName(StringUtils.defaultIfBlank(entity.getOsName(), "Ubuntu"));
                entity.setOsVersion(StringUtils.defaultIfBlank(entity.getOsVersion(), StringUtils.trimToNull(token.substring("ubuntu".length()))));
                continue;
            }
            if (StringUtils.isBlank(entity.getCudaVersion()) && lowerToken.startsWith("cuda")) {
                entity.setCudaVersion(StringUtils.trimToNull(token.substring("cuda".length())));
            }
        }
    }

    private String parseComponentVersion(String componentName, String firstToken) {
        if (StringUtils.isBlank(firstToken)) {
            return null;
        }
        if (StringUtils.isNotBlank(componentName) && firstToken.toLowerCase(Locale.ROOT).startsWith(componentName.toLowerCase(Locale.ROOT))) {
            return StringUtils.trimToNull(firstToken.substring(componentName.length()).replaceFirst("^:", ""));
        }
        return firstToken;
    }

    private String composeBaseImage(GpuComponentEntity entity) {
        String componentLabel = composeComponentLabel(entity);
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(componentLabel)) {
            parts.add(componentLabel);
        }
        if (StringUtils.isNotBlank(entity.getPythonVersion())) {
            parts.add("Python " + entity.getPythonVersion());
        }
        String osLabel = composeOsLabel(entity);
        if (StringUtils.isNotBlank(osLabel)) {
            parts.add(osLabel);
        }
        if (StringUtils.isNotBlank(entity.getCudaVersion())) {
            parts.add("CUDA " + entity.getCudaVersion());
        }
        if (ObjectUtils.isEmpty(parts)) {
            return StringUtils.trimToNull(entity.getBaseImage());
        }
        return String.join(" + ", parts);
    }

    private String composeComponentLabel(GpuComponentEntity entity) {
        if (StringUtils.isNotBlank(entity.getComponentName()) && StringUtils.isNotBlank(entity.getComponentVersion())) {
            return entity.getComponentName() + " " + entity.getComponentVersion();
        }
        return StringUtils.defaultIfBlank(entity.getComponentName(), entity.getComponentVersion());
    }

    private String composeOsLabel(GpuComponentEntity entity) {
        if (StringUtils.isNotBlank(entity.getOsName()) && StringUtils.isNotBlank(entity.getOsVersion())) {
            return entity.getOsName() + " " + entity.getOsVersion();
        }
        return StringUtils.defaultIfBlank(entity.getOsName(), entity.getOsVersion());
    }
}
