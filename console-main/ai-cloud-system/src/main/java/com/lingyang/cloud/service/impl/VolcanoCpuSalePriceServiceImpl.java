package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.InstanceResourceAdapterClient;
import com.lingyang.cloud.entity.VolcanoCpuSalePriceEntity;
import com.lingyang.cloud.mapper.VolcanoCpuSalePriceMapper;
import com.lingyang.cloud.model.edit.gpu.VolcanoCpuSalePriceEdit;
import com.lingyang.cloud.model.vo.system.VolcanoCpuSalePriceVO;
import com.lingyang.cloud.service.VolcanoCpuSalePriceService;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.security.SecurityContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Service
public class VolcanoCpuSalePriceServiceImpl implements VolcanoCpuSalePriceService {
    private final VolcanoCpuSalePriceMapper mapper;
    private final JdbcTemplate jdbc;
    private final TransactionTemplate transactions;
    private final InstanceResourceAdapterClient client;
    private final AtomicBoolean refreshing = new AtomicBoolean();

    public VolcanoCpuSalePriceServiceImpl(VolcanoCpuSalePriceMapper mapper, JdbcTemplate jdbc,
            TransactionTemplate transactions, InstanceResourceAdapterClient client) {
        this.mapper = mapper;
        this.jdbc = jdbc;
        this.transactions = transactions;
        this.client = client;
    }

    @Override
    public JSONObject refreshCatalog() {
        if (!refreshing.compareAndSet(false, true)) throw new HttpServiceException("CPU 价格正在刷新，请稍后再试");
        try {
            JSONObject regions = client.listCpuRegions();
            if (!Boolean.TRUE.equals(regions.getBoolean("success")) || regions.getJSONArray("regions") == null) {
                throw new HttpServiceException("CPU 地域目录暂不可用，已保留原价格");
            }
            JSONArray results = new JSONArray();
            int succeeded = 0;
            for (Object value : regions.getJSONArray("regions")) {
                JSONObject region = (JSONObject) value;
                String code = region.getString("region");
                JSONObject result = new JSONObject();
                result.put("region", code);
                try {
                    if (StringUtils.isBlank(code)) throw new HttpServiceException("地域编码缺失");
                    JSONObject catalog = client.listCpuPrices(code, true);
                    if (!Boolean.TRUE.equals(catalog.getBoolean("success")) || catalog.getJSONArray("specs") == null) {
                        throw new HttpServiceException("地域价格读取失败");
                    }
                    catalog.put("label", region.get("label"));
                    catalog.put("zones", region.get("zones"));
                    catalog.put("region", code);
                    result.put("specCount", catalog.getJSONArray("specs").size());
                    result.put("pricing", catalog.get("pricing"));
                    String pricingStatus = catalog.getJSONObject("pricing") == null ? "ok" : catalog.getJSONObject("pricing").getString("status");
                    result.put("message", "ok".equals(pricingStatus) ? "价格已更新" : "部分价格暂不可用，缺价规格已隐藏");
                    result.put("success", true);
                    transactions.executeWithoutResult(tx -> storeRegion(code, catalog, result));
                    succeeded++;
                } catch (Exception error) {
                    result.put("success", false);
                    result.put("message", "刷新失败，已保留上次价格，请稍后重试");
                    // Record the failed attempt without replacing the last usable catalog.
                    jdbc.update("UPDATE volcano_cpu_catalog_snapshot SET last_refresh_result=? WHERE region_code=?",
                            result.toJSONString(), code);
                }
                results.add(result);
            }
            JSONObject summary = new JSONObject();
            summary.put("success", succeeded == results.size());
            summary.put("succeeded", succeeded);
            summary.put("failed", results.size() - succeeded);
            summary.put("regions", results);
            summary.put("message", "刷新完成：成功 " + succeeded + " 个地域，失败 " + (results.size() - succeeded) + " 个地域");
            return summary;
        } finally { refreshing.set(false); }
    }

    private void storeRegion(String region, JSONObject catalog, JSONObject result) {
        String version = UUID.randomUUID().toString();
        // Lock the same regional row used by administrative edits, avoiding stale save/refresh races.
        jdbc.update("INSERT INTO volcano_cpu_catalog_snapshot(region_code,catalog_json,price_version) VALUES(?,?,?) ON DUPLICATE KEY UPDATE region_code=VALUES(region_code)", region, "{}", version);
        lockRegion(region);
        int raised = 0;
        List<VolcanoCpuSalePriceEntity> configs = mapper.selectList(Wrappers.lambdaQuery(VolcanoCpuSalePriceEntity.class)
                .eq(VolcanoCpuSalePriceEntity::getRegionCode, region).eq(VolcanoCpuSalePriceEntity::getDelFlag, 0));
        Map<String, JSONObject> specs = new HashMap<>();
        for (Object value : catalog.getJSONArray("specs")) {
            JSONObject spec = (JSONObject) value;
            specs.put(spec.getString("instanceTypeId"), spec);
        }
        for (VolcanoCpuSalePriceEntity config : configs) {
            JSONObject spec = specs.get(config.getInstanceTypeId());
            BigDecimal upstream = upstream(spec, config.getBillingType());
            if (upstream == null) continue;
            config.setUpstreamPrice(upstream);
            if (config.getSalePrice().compareTo(upstream) < 0) {
                config.setSalePrice(upstream);
                raised++;
            }
            config.setPriceVersion(version);
            config.setUpdateTime(new Date());
            mapper.updateById(config);
        }
        result.put("raisedPrices", raised);
        catalog.put("priceVersion", version);
        catalog.put("updatedAt", new Date());
        jdbc.update("UPDATE volcano_cpu_catalog_snapshot SET catalog_json=?,price_version=?,refreshed_at=CURRENT_TIMESTAMP,last_refresh_result=? WHERE region_code=?",
                catalog.toJSONString(), version, result.toJSONString(), region);
    }

    @Override
    public JSONObject getStoredCatalog() {
        JSONArray regions = new JSONArray();
        for (Map<String, Object> row : jdbc.queryForList("SELECT catalog_json,last_refresh_result FROM volcano_cpu_catalog_snapshot ORDER BY region_code")) {
            JSONObject catalog = JSON.parseObject((String) row.get("catalog_json"));
            catalog.put("lastRefreshResult", row.get("last_refresh_result") == null ? null
                    : JSON.parseObject((String) row.get("last_refresh_result")));
            inject(catalog, false, null);
            regions.add(catalog);
        }
        JSONObject result = new JSONObject();
        result.put("regions", regions);
        result.put("success", true);
        return result;
    }

    @Override
    public JSONObject getDisplayPrices(String region, String instanceTypeId, String billingMode) {
        String type = billingMode == null ? null : normalizeBilling(billingMode);
        JSONObject result = readRegion(region);
        inject(result, true, type);
        if (StringUtils.isNotBlank(instanceTypeId)) result.getJSONArray("specs").removeIf(value ->
                !instanceTypeId.equals(((JSONObject) value).getString("instanceTypeId")));
        return result;
    }

    private JSONObject readRegion(String region) {
        List<String> rows = jdbc.queryForList("SELECT catalog_json FROM volcano_cpu_catalog_snapshot WHERE region_code=?", String.class, region);
        if (rows.isEmpty()) {
            JSONObject empty = new JSONObject();
            empty.put("success", true);
            empty.put("region", region);
            empty.put("specs", new JSONArray());
            return empty;
        }
        return JSON.parseObject(rows.get(0));
    }

    private void inject(JSONObject catalog, boolean display, String billing) {
        JSONArray specs = catalog.getJSONArray("specs");
        if (specs == null) { catalog.put("specs", new JSONArray()); return; }
        List<VolcanoCpuSalePriceEntity> list = mapper.selectList(Wrappers.lambdaQuery(VolcanoCpuSalePriceEntity.class)
                .eq(VolcanoCpuSalePriceEntity::getRegionCode, catalog.getString("region")).eq(VolcanoCpuSalePriceEntity::getDelFlag, 0));
        Map<String, VolcanoCpuSalePriceEntity> configs = new HashMap<>();
        for (var config : list) configs.put(config.getInstanceTypeId() + ":" + config.getBillingType(), config);
        Iterator<Object> iterator = specs.iterator();
        while (iterator.hasNext()) {
            JSONObject spec = (JSONObject) iterator.next();
            BigDecimal hourly = upstream(spec, "on_demand"), monthly = upstream(spec, "monthly");
            var hourlyConfig = configs.get(spec.getString("instanceTypeId") + ":on_demand");
            var monthlyConfig = configs.get(spec.getString("instanceTypeId") + ":monthly");
            BigDecimal displayHourly = salePrice(hourly, hourlyConfig);
            BigDecimal displayMonthly = salePrice(monthly, monthlyConfig);
            if (display && (billing == null ? displayHourly == null && displayMonthly == null
                    : (normalizeBilling(billing).equals("monthly") ? displayMonthly : displayHourly) == null)) {
                iterator.remove(); continue;
            }
            for (String type : List.of("on_demand", "monthly")) {
                boolean month = type.equals("monthly");
                var config = configs.get(spec.getString("instanceTypeId") + ":" + type);
                BigDecimal original = month ? monthly : hourly;
                String field = month ? "salePriceMonthly" : "salePrice";
                spec.put(field, salePrice(original, config));
                if (config != null) {
                    if (!display) spec.put(month ? "monthlyPriceConfig" : "hourlyPriceConfig", toVO(config));
                    spec.put(month ? "monthlyPriceVersion" : "hourlyPriceVersion", config.getPriceVersion());
                }
            }
            spec.put("priceVersion", catalog.getString("priceVersion"));
        }
    }

    public static String normalizeBilling(String billing) {
        return switch (StringUtils.defaultString(billing)) {
            case "on_demand", "hourly", "PostPaid" -> "on_demand";
            case "monthly", "yearly", "PrePaid" -> "monthly";
            default -> throw new HttpServiceException("CPU 计费方式无效");
        };
    }

    public static BigDecimal upstream(JSONObject spec, String billing) {
        if (spec == null) return null;
        JSONObject price = spec.getJSONObject(normalizeBilling(billing).equals("monthly") ? "priceMonthly" : "price");
        if (price == null || !Boolean.TRUE.equals(price.getBoolean("available"))
                || Boolean.TRUE.equals(price.getBoolean("pricePending")) || !"CNY".equals(price.getString("currency"))) return null;
        BigDecimal value = price.getBigDecimal("unitPrice");
        return value != null && value.signum() > 0 ? value : null;
    }

    public static BigDecimal salePrice(BigDecimal upstream, VolcanoCpuSalePriceEntity config) {
        if (config != null && Objects.equals(1, config.getStatus()) && config.getSalePrice() != null
                && config.getSalePrice().signum() > 0) {
            return upstream == null ? config.getSalePrice() : config.getSalePrice().max(upstream);
        }
        return upstream;
    }

    @Override
    public JSONObject quote(JSONObject request) {
        String billing = normalizeBilling(request.getString("billingMode"));
        JSONObject catalog = getDisplayPrices(request.getString("region"), request.getString("instanceTypeId"), billing);
        JSONArray specs = catalog.getJSONArray("specs");
        if (specs.isEmpty()) throw new HttpServiceException("CPU 价格暂不可用或已变化，请刷新后重试");
        JSONArray zones = catalog.getJSONArray("zones");
        if (zones == null || !zones.contains(request.getString("zone"))) throw new HttpServiceException("可用区不属于当前 CPU 地域");
        JSONObject spec = specs.getJSONObject(0);
        BigDecimal original = upstream(spec, billing);
        if (original == null) throw new HttpServiceException("火山云 CPU 原价暂不可用，请刷新价格后重试");
        BigDecimal sale = spec.getBigDecimal(billing.equals("monthly") ? "salePriceMonthly" : "salePrice");
        String mode = request.getString("billingMode");
        int duration = "on_demand".equals(mode) ? 1 : request.getIntValue("duration");
        int periods = "yearly".equals(mode) ? Math.multiplyExact(duration, 12) : duration;
        int count = request.getIntValue("count");
        if (count < 1 || count > 10 || duration < 1 || duration > 36) throw new HttpServiceException("购买数量或时长无效");
        BigDecimal multiplier = BigDecimal.valueOf((long) count * periods);
        JSONObject pricing = new JSONObject();
        pricing.put("upstreamPrice", original);
        pricing.put("salePrice", sale);
        pricing.put("unit_price", sale);
        pricing.put("discount_unit_price", sale);
        pricing.put("premiumUnitAmount", sale.subtract(original));
        pricing.put("total_cost", sale.multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
        pricing.put("discount_total_cost", pricing.get("total_cost"));
        pricing.put("premiumAmount", sale.subtract(original).multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
        pricing.put("unit", billing.equals("monthly") ? "MONTH" : "HOUR");
        pricing.put("currency", "CNY");
        pricing.put("billingType", mode);
        pricing.put("chargeType", billing.equals("monthly") ? "PrePaid" : "PostPaid");
        pricing.put("quantity", count);
        pricing.put("duration", duration);
        pricing.put("billingPeriods", periods);
        pricing.put("priceVersion", StringUtils.defaultString(spec.getString(billing.equals("monthly") ? "monthlyPriceVersion" : "hourlyPriceVersion"), "default")
                + ":" + spec.getString("priceVersion"));
        return pricing;
    }

    @Override
    public PageResult<VolcanoCpuSalePriceVO> getPricePage(PageQuery page, String region, String instance, String billing, Integer status) {
        page.startPage();
        List<VolcanoCpuSalePriceEntity> rows = mapper.selectList(Wrappers.lambdaQuery(VolcanoCpuSalePriceEntity.class)
                .eq(VolcanoCpuSalePriceEntity::getDelFlag, 0)
                .eq(StringUtils.isNotBlank(region), VolcanoCpuSalePriceEntity::getRegionCode, region)
                .like(StringUtils.isNotBlank(instance), VolcanoCpuSalePriceEntity::getInstanceTypeId, instance)
                .eq(StringUtils.isNotBlank(billing), VolcanoCpuSalePriceEntity::getBillingType, billing)
                .eq(status != null, VolcanoCpuSalePriceEntity::getStatus, status)
                .orderByAsc(VolcanoCpuSalePriceEntity::getRegionCode, VolcanoCpuSalePriceEntity::getInstanceTypeId));
        return PageResult.of(rows.stream().map(this::toVO).toList());
    }

    @Override public VolcanoCpuSalePriceVO getById(Long id) { return toVO(require(id)); }

    @Override
    public void saveOrUpdate(VolcanoCpuSalePriceEdit edit) {
        inRegion(edit.getRegionCode(), () -> {
            String billing = normalizeBilling(edit.getBillingType());
            JSONObject catalog = readRegion(edit.getRegionCode());
            JSONObject spec = null;
            for (Object value : catalog.getJSONArray("specs")) if (edit.getInstanceTypeId().equals(((JSONObject)value).getString("instanceTypeId"))) spec = (JSONObject)value;
            BigDecimal original = upstream(spec, billing);
            if (original == null) throw new HttpServiceException("火山云 CPU 原价不可用，请先刷新价格");
            if (edit.getSalePrice() == null || edit.getSalePrice().scale() > 4 || edit.getSalePrice().precision() > 12
                    || edit.getSalePrice().compareTo(original) < 0) throw new HttpServiceException("平台售价不能低于火山云原价 " + original + "，且最多4位小数");
            if (edit.getStatus() != null && edit.getStatus() != 0 && edit.getStatus() != 1) throw new HttpServiceException("售价状态无效");
            var existing = mapper.selectByUniqueKey(edit.getRegionCode(), edit.getInstanceTypeId(), billing);
            if (edit.getId() != null && (existing == null || !edit.getId().equals(existing.getId()))) throw new HttpServiceException("售价配置已变化，请刷新后重试");
            VolcanoCpuSalePriceEntity entity = existing == null ? new VolcanoCpuSalePriceEntity() : existing;
            entity.setRegionCode(edit.getRegionCode()); entity.setInstanceTypeId(edit.getInstanceTypeId()); entity.setBillingType(billing);
            entity.setFamily(spec.getString("family")); entity.setCpuCores(spec.getInteger("cpuCores"));
            entity.setCpuModel(spec.getString("cpuModel")); entity.setMemGib(spec.getBigDecimal("memGib"));
            entity.setUpstreamPrice(original); entity.setSalePrice(edit.getSalePrice()); entity.setCurrency("CNY");
            entity.setStatus(edit.getStatus() == null ? 1 : edit.getStatus()); entity.setRemark(edit.getRemark()); entity.setDelFlag(0);
            entity.setPriceVersion(UUID.randomUUID().toString());
            Date now = new Date(); entity.setUpdateTime(now); if (existing == null) entity.setCreateTime(now);
            entity.setDelFlag(0);
            var user = SecurityContext.getUserInfo();
            entity.setUpdateById(user.getUserId()); entity.setUpdateBy(user.getUsername());
            if (existing == null) { entity.setCreateById(user.getUserId()); entity.setCreateBy(user.getUsername()); mapper.insert(entity); }
            else mapper.updateById(entity);
            return null;
        });
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (status == null || (status != 0 && status != 1)) throw new HttpServiceException("售价状态无效");
        var existing = require(id);
        inRegion(existing.getRegionCode(), () -> {
            VolcanoCpuSalePriceEntity entity = require(id);
            if (status == 0) {
                entity.setStatus(0); entity.setPriceVersion(UUID.randomUUID().toString()); entity.setUpdateTime(new Date());
                entity.setUpdateById(SecurityContext.getUserInfo().getUserId()); entity.setUpdateBy(SecurityContext.getUserInfo().getUsername());
                mapper.updateById(entity);
            } else {
                VolcanoCpuSalePriceEdit edit = new VolcanoCpuSalePriceEdit();
                BeanUtils.copyProperties(entity, edit); edit.setStatus(status); saveOrUpdate(edit);
            }
            return null;
        });
    }

    @Override public void restoreDefaultPrice(Long id) {
        var entity = require(id);
        inRegion(entity.getRegionCode(), () -> { mapper.deletePhysicalById(id); return null; });
    }

    private <T> T inRegion(String region, Supplier<T> action) {
        return transactions.execute(tx -> { lockRegion(region); return action.get(); });
    }
    private void lockRegion(String region) {
        var rows = jdbc.queryForList("SELECT region_code FROM volcano_cpu_catalog_snapshot WHERE region_code=? FOR UPDATE", region);
        if (rows.isEmpty()) throw new HttpServiceException("CPU 地域价格尚未同步，请先刷新");
    }
    private VolcanoCpuSalePriceEntity require(Long id) {
        var entity = mapper.selectById(id);
        if (entity == null || !Objects.equals(0, entity.getDelFlag())) throw new HttpServiceException("CPU 售价配置不存在");
        return entity;
    }
    private VolcanoCpuSalePriceVO toVO(VolcanoCpuSalePriceEntity entity) {
        VolcanoCpuSalePriceVO vo = new VolcanoCpuSalePriceVO(); BeanUtils.copyProperties(entity, vo);
        vo.setPremium(entity.getUpstreamPrice() == null ? null : entity.getSalePrice().subtract(entity.getUpstreamPrice()));
        return vo;
    }
}
