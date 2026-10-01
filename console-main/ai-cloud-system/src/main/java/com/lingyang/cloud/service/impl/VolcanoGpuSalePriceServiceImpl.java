package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.entity.VolcanoGpuCatalogSnapshotEntity;
import com.lingyang.cloud.entity.VolcanoGpuSalePriceEntity;
import com.lingyang.cloud.mapper.VolcanoGpuCatalogSnapshotMapper;
import com.lingyang.cloud.mapper.VolcanoGpuSalePriceMapper;
import com.lingyang.cloud.model.edit.gpu.VolcanoGpuSalePriceEdit;
import com.lingyang.cloud.model.vo.system.VolcanoGpuSalePriceVO;
import com.lingyang.cloud.service.VolcanoGpuSalePriceService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.security.SecurityContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.time.LocalDateTime;

/**
 * 火山云 GPU 平台售价服务实现
 */
@Service
@Slf4j
public class VolcanoGpuSalePriceServiceImpl implements VolcanoGpuSalePriceService {

    private static final Set<String> BILLING_TYPES = Set.of("on_demand", "hourly", "daily", "weekly", "monthly");

    private static final Set<String> DISPLAY_BILLING_TYPES = Set.of("all", "on_demand", "hourly", "monthly");

    @Resource
    private VolcanoGpuSalePriceMapper salePriceMapper;

    @Resource
    private GpuSchedulerApiClient gpuSchedulerApiClient;

    @Resource
    private VolcanoGpuCatalogSnapshotMapper catalogSnapshotMapper;

    @Override
    public PageResult<VolcanoGpuSalePriceVO> getPricePage(PageQuery pageQuery, String regionCode,
            String instanceTypeId, String gpuModel, String billingType, Integer status) {
        pageQuery.startPage();
        LambdaQueryWrapper<VolcanoGpuSalePriceEntity> wrapper = Wrappers.lambdaQuery(VolcanoGpuSalePriceEntity.class)
                .eq(VolcanoGpuSalePriceEntity::getDelFlag, 0)
                .like(StringUtils.isNotBlank(regionCode), VolcanoGpuSalePriceEntity::getRegionCode, regionCode)
                .like(StringUtils.isNotBlank(instanceTypeId), VolcanoGpuSalePriceEntity::getInstanceTypeId, instanceTypeId)
                .like(StringUtils.isNotBlank(gpuModel), VolcanoGpuSalePriceEntity::getGpuModel, gpuModel)
                .eq(StringUtils.isNotBlank(billingType), VolcanoGpuSalePriceEntity::getBillingType, billingType)
                .eq(status != null, VolcanoGpuSalePriceEntity::getStatus, status)
                .orderByAsc(VolcanoGpuSalePriceEntity::getRegionCode)
                .orderByAsc(VolcanoGpuSalePriceEntity::getGpuModel)
                .orderByAsc(VolcanoGpuSalePriceEntity::getInstanceTypeId)
                .orderByAsc(VolcanoGpuSalePriceEntity::getBillingType);
        List<VolcanoGpuSalePriceEntity> list = salePriceMapper.selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return PageResult.of(new ArrayList<>());
        }
        return PageResult.of(list.stream().map(this::toVO).toList());
    }

    @Override
    public VolcanoGpuSalePriceVO getById(Long id) {
        VolcanoGpuSalePriceEntity entity = requireConfig(id);
        return toVO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(VolcanoGpuSalePriceEdit edit) {
        validateEdit(edit);
        VolcanoGpuSalePriceEntity entity = new VolcanoGpuSalePriceEntity();
        BeanUtils.copyProperties(edit, entity);
        entity.setCurrency("CNY");
        entity.setGpuCount(entity.getGpuCount() == null ? 0 : entity.getGpuCount());
        entity.setStatus(entity.getStatus() == null ? 1 : entity.getStatus());
        fillAudit(entity);

        VolcanoGpuSalePriceEntity existing = salePriceMapper.selectByUniqueKey(
                entity.getRegionCode(), entity.getInstanceTypeId(), entity.getBillingType());
        if (existing != null && edit.getId() != null && !Objects.equals(existing.getId(), edit.getId())) {
            throw new HttpServiceException("相同地域、实例规格和计费类型的售价已存在");
        }
        if (existing != null) {
            entity.setId(existing.getId());
            salePriceMapper.updateById(entity);
            return;
        }
        if (edit.getId() != null) {
            requireConfig(edit.getId());
            entity.setId(edit.getId());
            salePriceMapper.updateById(entity);
            return;
        }
        salePriceMapper.insert(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, Integer status) {
        if (!Objects.equals(status, 0) && !Objects.equals(status, 1)) {
            throw new HttpServiceException("售价状态无效");
        }
        VolcanoGpuSalePriceEntity entity = new VolcanoGpuSalePriceEntity();
        entity.setId(requireConfig(id).getId());
        entity.setStatus(status);
        fillAudit(entity);
        salePriceMapper.updateById(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreDefaultPrice(Long id) {
        requireConfig(id);
        salePriceMapper.deletePhysicalById(id);
    }

    @Override
    public VolcanoGpuSalePriceEntity resolvePrice(String regionCode, String instanceTypeId, String billingType) {
        if (StringUtils.isAnyBlank(regionCode, instanceTypeId, billingType)) {
            return null;
        }
        String normalized = normalizeBillingType(billingType);
        VolcanoGpuSalePriceEntity entity = salePriceMapper.selectByUniqueKey(
                regionCode, instanceTypeId, normalized);
        if (entity == null && "on_demand".equals(normalized)) {
            entity = salePriceMapper.selectByUniqueKey(regionCode, instanceTypeId, "hourly");
        }
        return enabledOrNull(entity);
    }

    @Override
    public BigDecimal resolveSalePrice(String regionCode, String instanceTypeId, String billingType, BigDecimal upstreamPrice) {
        VolcanoGpuSalePriceEntity entity = resolvePrice(regionCode, instanceTypeId, billingType);
        return entity == null ? upstreamPrice : entity.getSalePrice();
    }

    @Override
    public JSONObject injectSalePrices(JSONObject catalog) {
        if (catalog == null || catalog.getJSONArray("regions") == null) {
            return catalog;
        }
        Map<String, VolcanoGpuSalePriceEntity> priceMap = new HashMap<>();
        for (Object regionItem : catalog.getJSONArray("regions")) {
            JSONObject region = toJSONObject(regionItem);
            if (region == null) continue;
            String regionCode = StringUtils.defaultString(region.getString("region"));
            JSONArray specs = region.getJSONArray("gpuSpecs");
            if (specs == null) continue;
            for (Object specItem : specs) {
                JSONObject spec = toJSONObject(specItem);
                if (spec == null || spec.getJSONArray("instanceTypes") == null) continue;
                for (Object instanceItem : spec.getJSONArray("instanceTypes")) {
                    JSONObject instance = toJSONObject(instanceItem);
                    if (instance == null) continue;
                    String instanceTypeId = instance.getString("instanceTypeId");
                    if (StringUtils.isBlank(instanceTypeId)) instanceTypeId = instance.getString("instance_type_id");
                    if (StringUtils.isBlank(instanceTypeId)) continue;
                    for (String billingType : BILLING_TYPES.stream().sorted().toList()) {
                        VolcanoGpuSalePriceEntity entity = resolvePrice(regionCode, instanceTypeId, billingType);
                        if (entity != null) {
                            priceMap.put(key(regionCode, instanceTypeId, billingType), entity);
                        }
                    }
                }
            }
        }

        for (Object regionItem : catalog.getJSONArray("regions")) {
            JSONObject region = toJSONObject(regionItem);
            if (region == null || region.getJSONArray("gpuSpecs") == null) continue;
            String regionCode = StringUtils.defaultString(region.getString("region"));
            for (Object specItem : region.getJSONArray("gpuSpecs")) {
                JSONObject spec = toJSONObject(specItem);
                if (spec == null || spec.getJSONArray("instanceTypes") == null) continue;
                for (Object instanceItem : spec.getJSONArray("instanceTypes")) {
                    JSONObject instance = toJSONObject(instanceItem);
                    if (instance == null) continue;
                    String instanceTypeId = instance.getString("instanceTypeId");
                    if (StringUtils.isBlank(instanceTypeId)) instanceTypeId = instance.getString("instance_type_id");
                    if (StringUtils.isBlank(instanceTypeId)) continue;
                    VolcanoGpuSalePriceEntity hourlyConfig = priceMap.get(key(regionCode, instanceTypeId, "on_demand"));
                    if (hourlyConfig == null) {
                        hourlyConfig = priceMap.get(key(regionCode, instanceTypeId, "hourly"));
                    }
                    applySalePrice(instance, "salePrice",
                            hourlyConfig,
                            firstPositive(toBigDecimal(instance.get("price")), spec == null ? null : toBigDecimal(spec.get("price"))));
                    if (hourlyConfig != null) {
                        instance.put("hourlyPriceConfigId", hourlyConfig.getId());
                    }
                    applySalePrice(instance, "salePriceMonthly",
                            priceMap.get(key(regionCode, instanceTypeId, "monthly")),
                            firstPositive(toBigDecimal(instance.get("priceMonthly")), spec == null ? null : toBigDecimal(spec.get("priceMonthly"))));
                    if (priceMap.containsKey(key(regionCode, instanceTypeId, "monthly"))) {
                        instance.put("monthlyPriceConfigId", priceMap.get(key(regionCode, instanceTypeId, "monthly")).getId());
                    }
                }
            }
        }
        return catalog;
    }

    @Override
    public JSONObject getDisplayCatalog(String billingType) {
        String billingScope = normalizeCatalogScope(billingType);
        if (billingScope == null) return emptyCatalog("unsupported");
        VolcanoGpuCatalogSnapshotEntity snapshot = selectCatalogSnapshot(billingScope);
        if (snapshot != null && StringUtils.isNotBlank(snapshot.getCatalogJson())) {
            try {
                return injectSalePrices(JSON.parseObject(snapshot.getCatalogJson()));
            } catch (Exception e) {
                log.error("解析火山云目录快照失败，billingScope={}", billingScope, e);
            }
        }

        JSONObject catalog = gpuSchedulerApiClient.getGpuCatalog(billingScope);
        saveCatalogSnapshot(billingScope, catalog);
        return injectSalePrices(catalog);
    }

    @Override
    public JSONObject getStoredCatalog(String billingType) {
        String billingScope = normalizeCatalogScope(billingType);
        VolcanoGpuCatalogSnapshotEntity snapshot = selectCatalogSnapshot(billingScope);
        if (snapshot == null || StringUtils.isBlank(snapshot.getCatalogJson())) {
            return emptyCatalog("missing");
        }
        try {
            JSONObject catalog = injectSalePrices(JSON.parseObject(snapshot.getCatalogJson()));
            catalog.put("snapshotStatus", "ok");
            catalog.put("snapshotTime", snapshot.getRefreshTime());
            return catalog;
        } catch (Exception e) {
            log.error("解析火山云目录快照失败，billingScope={}", billingScope, e);
            return emptyCatalog("invalid");
        }
    }

    @Override
    public BigDecimal findSnapshotGpuPrice(String regionCode, String gpuModel, String gpuMemory,
            String instanceTypeId, String billingType) {
        if (StringUtils.isAnyBlank(regionCode, instanceTypeId)) {
            return null;
        }
        String normalizedBillingType = normalizeBillingType(billingType);
        // 目录快照可能按具体计费方式保存，也可能只保存了 all 快照。
        // 优先使用精确快照，再回退到 all；整个过程只读数据库，不触发上游请求。
        List<String> scopes = new ArrayList<>();
        if (DISPLAY_BILLING_TYPES.contains(normalizedBillingType)) {
            scopes.add(normalizedBillingType);
        }
        if (!scopes.contains("all")) {
            scopes.add("all");
        }
        for (String scope : scopes) {
            VolcanoGpuCatalogSnapshotEntity snapshot = selectCatalogSnapshot(scope);
            BigDecimal price = findPriceInSnapshot(snapshot, regionCode, gpuModel, gpuMemory,
                    instanceTypeId, normalizedBillingType);
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                return price;
            }
        }
        return null;
    }

    private BigDecimal findPriceInSnapshot(VolcanoGpuCatalogSnapshotEntity snapshot, String regionCode,
            String gpuModel, String gpuMemory, String instanceTypeId, String billingType) {
        if (snapshot == null || StringUtils.isBlank(snapshot.getCatalogJson())) {
            return null;
        }
        try {
            JSONObject catalog = JSON.parseObject(snapshot.getCatalogJson());
            JSONArray regions = catalog == null ? null : catalog.getJSONArray("regions");
            if (regions == null) return null;
            boolean monthly = "monthly".equalsIgnoreCase(billingType);
            for (Object regionItem : regions) {
                JSONObject region = toJSONObject(regionItem);
                if (region == null || !StringUtils.equals(regionCode,
                        getString(region, "region", "regionCode", "region_code"))) continue;
                JSONArray specs = region.getJSONArray("gpuSpecs");
                if (specs == null) continue;
                for (Object specItem : specs) {
                    JSONObject spec = toJSONObject(specItem);
                    if (spec == null || (StringUtils.isNotBlank(gpuModel)
                            && !StringUtils.equals(gpuModel, getString(spec, "gpuModel", "gpu_model")))) continue;
                    if (StringUtils.isNotBlank(gpuMemory)
                            && !StringUtils.equals(gpuMemory, getString(spec, "gpuMemory", "gpu_memory"))) continue;
                    JSONArray instances = spec.getJSONArray("instanceTypes");
                    if (instances == null) continue;
                    for (Object instanceItem : instances) {
                        JSONObject instance = toJSONObject(instanceItem);
                        if (instance == null || !StringUtils.equals(instanceTypeId,
                                getString(instance, "instanceTypeId", "instance_type_id"))) continue;
                        Object value = monthly ? instance.get("priceMonthly") : instance.get("price");
                        BigDecimal price = priceNumber(value);
                        if (price == null) {
                            price = priceNumber(monthly ? spec.get("priceMonthly") : spec.get("price"));
                        }
                        return price;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("解析火山云 GPU 价格快照失败，billingScope={}, regionCode={}, instanceTypeId={}",
                    snapshot.getBillingScope(), regionCode, instanceTypeId, e);
        }
        return null;
    }

    private String getString(JSONObject object, String... names) {
        for (String name : names) {
            String value = object.getString(name);
            if (StringUtils.isNotBlank(value)) return value;
        }
        return null;
    }

    private BigDecimal priceNumber(Object value) {
        BigDecimal direct = toBigDecimal(value);
        if (direct != null) return direct;
        JSONObject object = toJSONObject(value);
        if (object == null) return null;
        for (String name : List.of("unitPrice", "unit_price", "price", "amount", "value")) {
            BigDecimal nested = toBigDecimal(object.get(name));
            if (nested != null) return nested;
        }
        return null;
    }

    @Override
    public JSONObject refreshCatalog(String billingType) {
        String billingScope = normalizeCatalogScope(billingType);
        if (billingScope == null) throw new HttpServiceException("计费类型无效");
        JSONObject catalog = gpuSchedulerApiClient.getGpuCatalog(billingScope);
        if (catalog == null || catalog.getJSONArray("regions") == null || catalog.getJSONArray("regions").isEmpty()) {
            throw new HttpServiceException("火山云 GPU 目录暂时不可用或为空");
        }
        if (!saveCatalogSnapshot(billingScope, catalog)) {
            throw new HttpServiceException("火山云 GPU 目录快照保存失败");
        }
        JSONObject result = injectSalePrices(catalog);
        result.put("snapshotStatus", "refreshed");
        return result;
    }

    private String normalizeCatalogScope(String billingType) {
        String billingScope = StringUtils.defaultIfBlank(billingType, "all").trim().toLowerCase();
        return DISPLAY_BILLING_TYPES.contains(billingScope) ? billingScope : null;
    }

    private VolcanoGpuCatalogSnapshotEntity selectCatalogSnapshot(String billingScope) {
        if (billingScope == null) return null;
        return catalogSnapshotMapper.selectOne(
                Wrappers.lambdaQuery(VolcanoGpuCatalogSnapshotEntity.class)
                        .eq(VolcanoGpuCatalogSnapshotEntity::getBillingScope, billingScope)
                        .orderByDesc(VolcanoGpuCatalogSnapshotEntity::getRefreshTime)
                        .last("limit 1"));
    }

    private JSONObject emptyCatalog(String snapshotStatus) {
        JSONObject catalog = new JSONObject();
        catalog.put("regions", new JSONArray());
        catalog.put("snapshotStatus", snapshotStatus);
        return catalog;
    }

    private boolean saveCatalogSnapshot(String billingScope, JSONObject catalog) {
        if (catalog == null) {
            return false;
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            VolcanoGpuCatalogSnapshotEntity entity = catalogSnapshotMapper.selectOne(
                    Wrappers.lambdaQuery(VolcanoGpuCatalogSnapshotEntity.class)
                            .eq(VolcanoGpuCatalogSnapshotEntity::getBillingScope, billingScope)
                            .last("limit 1"));
            if (entity == null) {
                entity = new VolcanoGpuCatalogSnapshotEntity();
                entity.setBillingScope(billingScope);
                entity.setCreateTime(now);
            }
            entity.setCatalogJson(catalog.toJSONString());
            entity.setSource(catalog.getString("source"));
            entity.setRefreshTime(now);
            entity.setUpdateTime(now);
            if (entity.getId() == null) {
                catalogSnapshotMapper.insert(entity);
            } else {
                catalogSnapshotMapper.updateById(entity);
            }
            return true;
        } catch (Exception e) {
            log.error("保存火山云目录快照失败，billingScope={}", billingScope, e);
            return false;
        }
    }


    private void applySalePrice(JSONObject instance, String fieldName, VolcanoGpuSalePriceEntity config, BigDecimal upstreamPrice) {
        BigDecimal salePrice = config == null ? upstreamPrice : config.getSalePrice();
        if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0) {
            instance.put(fieldName, salePrice);
            if (config != null) {
                instance.put("priceConfigured", true);
                instance.put("priceConfigModified", true);
            }
            instance.put("priceConfigId", config == null ? null : config.getId());
            instance.put(fieldName.equals("salePriceMonthly") ? "monthlyPriceConfigStatus" : "priceConfigStatus",
                    config == null ? "default" : (isEnabled(config) ? "enabled" : "disabled"));
        }
    }

    private VolcanoGpuSalePriceEntity enabledOrNull(VolcanoGpuSalePriceEntity entity) {
        return entity != null && isEnabled(entity) ? entity : null;
    }

    private boolean isEnabled(VolcanoGpuSalePriceEntity entity) {
        return entity != null && Objects.equals(entity.getStatus(), 1);
    }

    private String key(String regionCode, String instanceTypeId, String billingType) {
        return regionCode + "|" + instanceTypeId + "|" + billingType;
    }

    private String normalizeBillingType(String billingType) {
        return StringUtils.defaultIfBlank(billingType, "hourly").trim().toLowerCase();
    }

    private BigDecimal firstPositive(BigDecimal... values) {
        for (BigDecimal value : values) {
            if (value != null && value.compareTo(BigDecimal.ZERO) > 0) return value;
        }
        return null;
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return null;
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private JSONObject toJSONObject(Object value) {
        if (value instanceof JSONObject object) return object;
        return null;
    }

    private void validateEdit(VolcanoGpuSalePriceEdit edit) {
        if (edit == null) throw new HttpServiceException("售价信息不能为空");
        if (!BILLING_TYPES.contains(StringUtils.defaultString(edit.getBillingType()).trim().toLowerCase())) {
            throw new HttpServiceException("计费类型无效");
        }
        if (edit.getGpuCount() != null && edit.getGpuCount() < 0) {
            throw new HttpServiceException("GPU卡数不能小于0");
        }
        edit.setRegionCode(StringUtils.trimToNull(edit.getRegionCode()));
        edit.setInstanceTypeId(StringUtils.trimToNull(edit.getInstanceTypeId()));
        edit.setGpuModel(StringUtils.trimToNull(edit.getGpuModel()));
        edit.setBillingType(StringUtils.defaultString(edit.getBillingType()).trim().toLowerCase());
    }

    private VolcanoGpuSalePriceEntity requireConfig(Long id) {
        if (id == null) throw new HttpServiceException("售价配置ID不能为空");
        VolcanoGpuSalePriceEntity entity = salePriceMapper.selectOne(Wrappers.lambdaQuery(VolcanoGpuSalePriceEntity.class)
                .eq(VolcanoGpuSalePriceEntity::getId, id)
                .eq(VolcanoGpuSalePriceEntity::getDelFlag, 0)
                .last("limit 1"));
        if (entity == null) throw new HttpServiceException("售价配置不存在");
        return entity;
    }

    private void fillAudit(VolcanoGpuSalePriceEntity entity) {
        try {
            Object userInfo = SecurityContext.getUserInfo();
            if (userInfo == null) return;
            java.lang.reflect.Method getUserId = userInfo.getClass().getMethod("getUserId");
            java.lang.reflect.Method getUsername = userInfo.getClass().getMethod("getUsername");
            Long userId = (Long) getUserId.invoke(userInfo);
            String username = (String) getUsername.invoke(userInfo);
            entity.setCreateById(userId);
            entity.setCreateBy(username);
            entity.setUpdateById(userId);
            entity.setUpdateBy(username);
        } catch (Exception ignored) {
            // 定时任务等无登录上下文的调用允许不写操作人。
        }
    }

    private VolcanoGpuSalePriceVO toVO(VolcanoGpuSalePriceEntity entity) {
        VolcanoGpuSalePriceVO vo = new VolcanoGpuSalePriceVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
