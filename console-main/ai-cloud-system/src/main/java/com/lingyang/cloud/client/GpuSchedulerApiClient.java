package com.lingyang.cloud.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.config.RestTemplateConfig;
import com.lingyang.cloud.config.GpuMarketProperties;
import com.lingyang.cloud.model.query.gpu.GpuClusterNodeQuery;
import com.lingyang.cloud.model.query.gpu.GpuClusterQuery;
import com.lingyang.cloud.model.query.gpu.GpuNodePoolQuery;
import com.lingyang.cloud.model.vo.system.GpuClusterVO;
import com.lingyang.cloud.model.vo.system.GpuNodePoolVO;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;

/**
 * GPU Resource Scheduler 后台接口客户端。
 */
@Slf4j
@Component
public class GpuSchedulerApiClient {

    @Value("${gpu.scheduler.base-url:http://124.174.46.14:8000}")
    private String baseUrl;

    @Value("${gpu-pod.admin-auth-key:}")
    private String adminAuthKey;

    @Value("${gpu.scheduler.auth-key:}")
    private String schedulerAuthKey;

    @Value("${gpu.scheduler.connect-timeout:20000}")
    private Integer connectTimeout;

    @Value("${gpu.scheduler.read-timeout:20000}")
    private Integer readTimeout;

    /** 火山云目录变化频率低，默认缓存 5 分钟，避免每次打开页面都请求三个上游接口。 */
    @Value("${gpu.scheduler.catalog-cache-ttl-seconds:300}")
    private long catalogCacheTtlSeconds;

    @Value("${gpu.scheduler.catalog-cache-key-prefix:gpu:market:catalog:}")
    private String catalogCacheKeyPrefix;

    private RestTemplate restTemplate;

    @Resource
    private GpuPodTokenManager tokenManager;

    @Resource
    private GpuPodTenantProvider tenantProvider;

    @Resource
    private GpuMarketProperties gpuMarketProperties;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    /** 同一实例首次加载时只允许一个请求访问上游，其他请求复用刚写入的缓存。 */
    private final Object catalogCacheLock = new Object();

    @PostConstruct
    public void init() {
        this.restTemplate = RestTemplateConfig.create(getClientHttpRequestFactory());
        log.info("GpuSchedulerApiClient initialized with baseUrl: {}", baseUrl);
    }

    public PageResult<GpuClusterVO> getClusterPage(GpuClusterQuery query, PageQuery pageQuery) {
        List<GpuClusterVO> clusters = fetchClusters(query == null ? null : query.getStatus());
        List<GpuClusterVO> filtered = clusters.stream()
                .filter(item -> contains(item.getClusterName(), query == null ? null : query.getClusterName()))
                .filter(item -> contains(item.getRegion(), query == null ? null : query.getRegion()))
                .filter(item -> contains(item.getStatus(), query == null ? null : query.getStatus()))
                .toList();
        return pageList(filtered, pageQuery);
    }

    public PageResult<JSONObject> getNodePage(GpuClusterNodeQuery query, PageQuery pageQuery) {
        List<JSONObject> nodes = fetchNodes(query == null ? null : query.getStatus()).stream()
                .filter(item -> contains(getString(item, "node_name", "nodeName"), query == null ? null : query.getNodeName()))
                .filter(item -> contains(getString(item, "gpu_model", "gpuModel"), query == null ? null : query.getGpuModel()))
                .filter(item -> contains(getString(item, "status"), query == null ? null : query.getStatus()))
                .toList();
        log.info("GPU scheduler node page resolved {} nodes after local filters", nodes.size());
        return pageList(nodes, pageQuery);
    }

    public PageResult<GpuNodePoolVO> getNodePoolPage(GpuNodePoolQuery query, PageQuery pageQuery) {
        Map<String, GpuNodePoolVO> poolMap = new LinkedHashMap<>();
        fetchNodes(query == null ? null : query.getStatus()).forEach(node -> {
            String gpuModel = defaultString(getString(node, "gpu_model", "gpuModel"), "未标记GPU型号");
            GpuNodePoolVO current = poolMap.computeIfAbsent(gpuModel, model -> {
                GpuNodePoolVO vo = new GpuNodePoolVO();
                vo.setPoolName(model + " 节点池");
                vo.setGpuModel(model);
                vo.setNodeCount(0);
                vo.setReadyNodeCount(0);
                vo.setGpuTotal(0);
                vo.setAllocatedGpus(0);
                vo.setAvailableGpus(0);
                vo.setCpuTotalCores(BigDecimal.ZERO);
                vo.setMemoryTotalGi(BigDecimal.ZERO);
                vo.setDiskTotalGi(BigDecimal.ZERO);
                vo.setStatus("Ready");
                return vo;
            });
            current.setNodeCount(current.getNodeCount() + 1);
            if ("Ready".equalsIgnoreCase(getString(node, "status"))) {
                current.setReadyNodeCount(current.getReadyNodeCount() + 1);
            } else {
                current.setStatus("异常");
            }
            current.setGpuTotal(current.getGpuTotal() + intValue(node, "gpu_count", "gpuCount"));
            current.setAllocatedGpus(current.getAllocatedGpus() + intValue(node, "allocated_gpus", "allocatedGpus"));
            current.setAvailableGpus(current.getAvailableGpus() + intValue(node, "available_gpus", "availableGpus"));
            current.setCpuTotalCores(current.getCpuTotalCores().add(decimalValue(objectValue(node, "cpu"), "total_cores", "totalCores")));
            current.setMemoryTotalGi(current.getMemoryTotalGi().add(decimalValue(objectValue(node, "memory"), "total_gi", "totalGi")));
            current.setDiskTotalGi(current.getDiskTotalGi().add(decimalValue(objectValue(node, "disk"), "total_gi", "totalGi")));
        });

        List<GpuNodePoolVO> pools = poolMap.values().stream()
                .filter(item -> contains(item.getGpuModel(), query == null ? null : query.getGpuModel()))
                .filter(item -> contains(item.getStatus(), query == null ? null : query.getStatus()))
                .toList();
        return pageList(pools, pageQuery);
    }

    public JSONObject getClusterSummary(String clusterId) {
        String url = buildSchedulerClusterSummaryUrl(clusterId);
        try {
            log.info("Fetching GPU scheduler cluster summary from {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class);
            return JSON.parseObject(response.getBody());
        } catch (Exception e) {
            log.error("Failed to fetch GPU scheduler cluster summary from {}", url, e);
            return new JSONObject();
        }
    }

    public JSONObject getMonitorOverview() {
        String url = buildSchedulerMonitorOverviewUrl();
        try {
            log.info("Fetching GPU scheduler monitor overview from {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createAdminHeaders()), String.class);
            return JSON.parseObject(response.getBody());
        } catch (Exception e) {
            log.error("Failed to fetch GPU scheduler monitor overview from {}", url, e);
            JSONObject empty = new JSONObject();
            empty.put("success", false);
            empty.put("metrics_source", "none");
            empty.put("summary", new JSONObject());
            empty.put("clusters", new JSONArray());
            return empty;
        }
    }

    /**
     * 获取并合并火山云 GPU 数据。
     *
     * <p>调度器分别提供完整规格目录和实时可用资源，后台统一请求后按
     * 地域、GPU 型号、显存和实例规格合并，前端只需要调用当前系统接口。</p>
     */
    public JSONObject getGpuCatalog() {
        return getGpuCatalog(null);
    }

    /**
     * 获取并合并 GPU 目录，可按当前计费方式过滤无价实例。
     * billingType 为空时保留完整目录（供目录管理等场景使用）。
     */
    public JSONObject getGpuCatalog(String billingType) {
        return loadGpuCatalogFromScheduler(billingType);
    }

    /**
     * 官网展示目录允许短时间缓存；下单价格校验仍调用 getGpuCatalog 获取实时数据。
     */
    public JSONObject getCachedGpuCatalog(String billingType) {
        String cacheKey = catalogCacheKey(billingType);
        JSONObject cached = readCatalogCache(cacheKey);
        if (cached != null) {
            return cached;
        }

        synchronized (catalogCacheLock) {
            cached = readCatalogCache(cacheKey);
            if (cached != null) {
                return cached;
            }
            JSONObject result = loadGpuCatalogFromScheduler(billingType);
            if (isCacheableCatalog(result)) {
                writeCatalogCache(cacheKey, result);
            }
            return result;
        }
    }

    private JSONObject loadGpuCatalogFromScheduler(String billingType) {
        // 三个公开只读接口相互独立，并行请求可显著降低首次加载耗时。
        CompletableFuture<JSONObject> availabilityFuture = CompletableFuture.supplyAsync(
                () -> fetchPublicGpuData("/api/v1/gpu/availability"));
        CompletableFuture<JSONObject> catalogFuture = CompletableFuture.supplyAsync(
                () -> fetchPublicGpuData("/api/v1/gpu/catalog"));
        CompletableFuture<JSONObject> optionsFuture = CompletableFuture.supplyAsync(
                () -> fetchPublicGpuData("/api/v1/gpu/volcano/options"));
        CompletableFuture.allOf(availabilityFuture, catalogFuture, optionsFuture).join();
        JSONObject availability = availabilityFuture.join();
        JSONObject catalog = catalogFuture.join();
        JSONObject options = optionsFuture.join();
        return filterHiddenRegions(mergeGpuCatalog(availability, catalog, options, billingType));
    }

    private String catalogCacheKey(String billingType) {
        String suffix = StringUtils.isBlank(billingType) ? "all" : billingType.trim().toLowerCase();
        return catalogCacheKeyPrefix + suffix;
    }

    private JSONObject readCatalogCache(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value instanceof JSONObject json) {
                return JSON.parseObject(json.toJSONString());
            }
            if (value instanceof String text && StringUtils.isNotBlank(text)) {
                return JSON.parseObject(text);
            }
            if (value != null) {
                return JSON.parseObject(JSON.toJSONString(value));
            }
        } catch (Exception e) {
            log.warn("读取 GPU 目录缓存失败，将直接请求调度器: {}", e.getMessage());
        }
        return null;
    }

    private void writeCatalogCache(String key, JSONObject value) {
        if (catalogCacheTtlSeconds <= 0) return;
        try {
            redisTemplate.opsForValue().set(key, value.toJSONString(), catalogCacheTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("写入 GPU 目录缓存失败，不影响本次请求: {}", e.getMessage());
        }
    }

    private boolean isCacheableCatalog(JSONObject value) {
        return value != null && !(Boolean.FALSE.equals(value.getBoolean("success"))
                && "unavailable".equalsIgnoreCase(value.getString("source")));
    }

    private JSONObject filterHiddenRegions(JSONObject catalog) {
        if (catalog == null || catalog.getJSONArray("regions") == null) {
            return catalog;
        }
        JSONArray visibleRegions = new JSONArray();
        for (Object item : catalog.getJSONArray("regions")) {
            JSONObject region = toJSONObject(item);
            if (region != null && !gpuMarketProperties.isHiddenRegion(getString(region, "region", "regionCode", "region_code"))) {
                visibleRegions.add(region);
            }
        }
        catalog.put("regions", visibleRegions);
        return catalog;
    }

    /** 根据聚合目录校验外部实例的当前实价。 */
    public BigDecimal findGpuPrice(String regionCode, String gpuModel, String gpuMemory,
            String instanceTypeId, String billingType) {
        JSONObject catalog = getGpuCatalog(billingType);
        boolean monthly = "monthly".equalsIgnoreCase(billingType);
        for (Object regionItem : catalog.getJSONArray("regions") == null ? new JSONArray() : catalog.getJSONArray("regions")) {
            JSONObject region = toJSONObject(regionItem);
            if (region == null || !StringUtils.equals(regionCode, getString(region, "region"))) continue;
            for (Object specItem : region.getJSONArray("gpuSpecs") == null ? new JSONArray() : region.getJSONArray("gpuSpecs")) {
                JSONObject spec = toJSONObject(specItem);
                if (spec == null || !StringUtils.equals(gpuModel, getString(spec, "gpuModel", "gpu_model"))) continue;
                if (StringUtils.isNotBlank(gpuMemory) && !StringUtils.equals(gpuMemory, getString(spec, "gpuMemory", "gpu_memory"))) continue;
                for (Object instanceItem : spec.getJSONArray("instanceTypes") == null ? new JSONArray() : spec.getJSONArray("instanceTypes")) {
                    JSONObject instance = toJSONObject(instanceItem);
                    if (instance != null && StringUtils.equals(instanceTypeId, getString(instance, "instanceTypeId", "instance_type_id"))) {
                        return priceNumber(monthly ? instance.get("priceMonthly") : instance.get("price"));
                    }
                }
            }
        }
        return null;
    }

    /** 外部接口为公开接口，不附加管理端鉴权参数。 */
    private JSONObject fetchPublicGpuData(String path) {
        String url = baseUrl + path;
        try {
            log.info("Fetching GPU data from {}", url);
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);
            JSONObject result = JSON.parseObject(response.getBody());
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
            log.error("Failed to fetch GPU data from {}", url, e);
        }
        JSONObject failed = new JSONObject();
        failed.put("success", false);
        failed.put("source", "unavailable");
        failed.put("regions", new JSONArray());
        failed.put("message", "GPU 数据暂时不可用");
        return failed;
    }

    private JSONObject mergeGpuCatalog(JSONObject availability, JSONObject catalog, JSONObject options, String billingType) {
        boolean hasAvailability = Boolean.TRUE.equals(availability.getBoolean("success"))
                && availability.getJSONArray("regions") != null;
        boolean hasCatalog = Boolean.TRUE.equals(catalog.getBoolean("success"))
                && catalog.getJSONArray("regions") != null;

        // 实时接口不可用时仍返回完整目录，避免后台短暂异常导致页面无数据。
        if (!hasAvailability && hasCatalog) {
            JSONObject fallback = JSON.parseObject(catalog.toJSONString());
            fallback.put("availabilityStatus", "unavailable");
            fallback.put("availabilityMessage", availability.getString("message"));
            normalizeCatalogPrices(fallback, options);
            fallback.put("optionsSource", options.getString("source"));
            return filterCatalogByBillingType(fallback, billingType);
        }
        if (!hasCatalog && hasAvailability) {
            JSONObject fallback = JSON.parseObject(availability.toJSONString());
            fallback.put("catalogStatus", "unavailable");
            fallback.put("catalogMessage", catalog.getString("message"));
            normalizeCatalogPrices(fallback, options);
            fallback.put("optionsSource", options.getString("source"));
            return filterCatalogByBillingType(fallback, billingType);
        }
        if (!hasAvailability && !hasCatalog) {
            JSONObject failed = new JSONObject();
            failed.put("success", false);
            failed.put("source", "unavailable");
            failed.put("regions", new JSONArray());
            failed.put("message", "GPU 数据暂时不可用");
            return failed;
        }

        JSONObject merged = JSON.parseObject(catalog.toJSONString());
        merged.put("source", catalog.getString("source"));
        merged.put("availabilitySource", availability.getString("source"));
        merged.put("availabilityUpdatedAt", availability.getString("updatedAt"));
        merged.put("mergeMode", "availability+catalog");
        merged.put("mergeMessage", "实时可用资源与完整 ECS 目录已合并");
        merged.put("regions", mergeRegions(availability.getJSONArray("regions"), catalog.getJSONArray("regions")));
        normalizeCatalogPrices(merged, options);
        merged.put("optionsSource", options.getString("source"));
        return filterCatalogByBillingType(merged, billingType);
    }

    private JSONObject filterCatalogByBillingType(JSONObject result, String billingType) {
        if (result == null || StringUtils.isBlank(billingType) || result.getJSONArray("regions") == null) {
            return result;
        }
        boolean monthly = "monthly".equalsIgnoreCase(billingType);
        JSONArray filteredRegions = new JSONArray();
        for (Object regionItem : result.getJSONArray("regions")) {
            JSONObject region = toJSONObject(regionItem);
            if (region == null || region.getJSONArray("gpuSpecs") == null) continue;
            JSONArray filteredSpecs = new JSONArray();
            for (Object specItem : region.getJSONArray("gpuSpecs")) {
                JSONObject spec = toJSONObject(specItem);
                if (spec == null || spec.getJSONArray("instanceTypes") == null) continue;
                JSONArray filteredInstances = new JSONArray();
                for (Object instanceItem : spec.getJSONArray("instanceTypes")) {
                    JSONObject instance = toJSONObject(instanceItem);
                    if (instance == null) continue;
                    Object price = monthly ? instance.get("priceMonthly") : instance.get("price");
                    BigDecimal numericPrice = priceNumber(price);
                    if (numericPrice != null && numericPrice.compareTo(BigDecimal.ZERO) > 0) filteredInstances.add(instance);
                }
                if (!filteredInstances.isEmpty()) {
                    spec.put("instanceTypes", filteredInstances);
                    filteredSpecs.add(spec);
                }
            }
            if (!filteredSpecs.isEmpty()) {
                region.put("gpuSpecs", filteredSpecs);
                filteredRegions.add(region);
            }
        }
        result.put("regions", filteredRegions);
        return result;
    }

    /**
     * 将外部接口返回的价格对象统一为前端可直接展示的数值。
     * options 实例价格优先，catalog 实例级价格其次，规格级价格作为兜底。
     */
    private void normalizeCatalogPrices(JSONObject result, JSONObject options) {
        if (result == null || result.getJSONArray("regions") == null) {
            return;
        }
        Map<String, JSONObject> optionInstances = new LinkedHashMap<>();
        if (options != null && options.getJSONArray("regions") != null) {
            for (Object regionItem : options.getJSONArray("regions")) {
                JSONObject region = toJSONObject(regionItem);
                if (region == null || region.getJSONArray("gpuSpecs") == null) continue;
                String regionCode = defaultString(getString(region, "region"), "");
                for (Object specItem : region.getJSONArray("gpuSpecs")) {
                    JSONObject spec = toJSONObject(specItem);
                    if (spec == null || spec.getJSONArray("instanceTypes") == null) continue;
                    String compositeSpecKey = regionCode + "|" + specKey(spec);
                    for (Object instanceItem : spec.getJSONArray("instanceTypes")) {
                        JSONObject instance = toJSONObject(instanceItem);
                        if (instance == null) continue;
                        String instanceId = getString(instance, "instanceTypeId", "instance_type_id");
                        if (StringUtils.isNotBlank(instanceId)) optionInstances.put(compositeSpecKey + "|" + instanceId, instance);
                    }
                }
            }
        }
        for (Object regionItem : result.getJSONArray("regions")) {
            JSONObject region = toJSONObject(regionItem);
            if (region == null || region.getJSONArray("gpuSpecs") == null) continue;
            String regionCode = defaultString(getString(region, "region"), "");
            for (Object specItem : region.getJSONArray("gpuSpecs")) {
                JSONObject spec = toJSONObject(specItem);
                if (spec == null || spec.getJSONArray("instanceTypes") == null) continue;
                String currentSpecKey = regionCode + "|" + specKey(spec);
                for (Object instanceItem : spec.getJSONArray("instanceTypes")) {
                    JSONObject instance = toJSONObject(instanceItem);
                    if (instance == null) continue;
                    String instanceId = getString(instance, "instanceTypeId", "instance_type_id");
                    JSONObject option = optionInstances.get(currentSpecKey + "|" + instanceId);
                    BigDecimal hourlyPrice = firstValidPrice(
                            option == null ? null : option.get("price"),
                            instance.get("price"),
                            spec.get("price"));
                    BigDecimal monthlyPrice = firstValidPrice(
                            option == null ? null : option.get("priceMonthly"),
                            instance.get("priceMonthly"),
                            spec.get("priceMonthly"));
                    instance.put("price", hourlyPrice);
                    instance.put("priceMonthly", monthlyPrice);
                    if (option != null) {
                        copyIfPresent(instance, option, "currency");
                        copyIfPresent(instance, option, "stockStatus");
                    }
                }
            }
        }
    }

    private void copyIfPresent(JSONObject target, JSONObject source, String key) {
        if (source.containsKey(key) && source.get(key) != null) target.put(key, source.get(key));
    }

    private BigDecimal priceNumber(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return new BigDecimal(String.valueOf(value));
        }
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        JSONObject object = toJSONObject(value);
        if (object == null) return null;
        Object unitPrice = object.get("unitPrice");
        if (unitPrice == null) unitPrice = object.get("unit_price");
        if (unitPrice == null) return null;
        try {
            return new BigDecimal(String.valueOf(unitPrice));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private BigDecimal firstValidPrice(Object... values) {
        for (Object value : values) {
            BigDecimal price = priceNumber(value);
            if (price != null && price.compareTo(BigDecimal.ZERO) > 0) return price;
        }
        return null;
    }

    private JSONArray mergeRegions(JSONArray availabilityRegions, JSONArray catalogRegions) {
        Map<String, JSONObject> catalogByRegion = new LinkedHashMap<>();
        for (Object item : catalogRegions) {
            JSONObject region = toJSONObject(item);
            if (region != null) {
                catalogByRegion.put(defaultString(getString(region, "region"), ""), region);
            }
        }

        JSONArray mergedRegions = new JSONArray();
        for (Object item : availabilityRegions) {
            JSONObject availableRegion = toJSONObject(item);
            if (availableRegion == null) {
                continue;
            }
            String regionCode = defaultString(getString(availableRegion, "region"), "");
            JSONObject catalogRegion = catalogByRegion.get(regionCode);
            JSONObject mergedRegion = catalogRegion == null
                    ? JSON.parseObject(availableRegion.toJSONString())
                    : JSON.parseObject(catalogRegion.toJSONString());
            if (availableRegion.getString("error") != null) {
                mergedRegion.put("error", availableRegion.getString("error"));
            }
            JSONArray availableSpecs = availableRegion.getJSONArray("gpuSpecs");
            JSONArray catalogSpecs = catalogRegion == null ? new JSONArray() : catalogRegion.getJSONArray("gpuSpecs");
            mergedRegion.put("gpuSpecs", mergeGpuSpecs(availableSpecs, catalogSpecs));
            mergedRegions.add(mergedRegion);
        }
        return mergedRegions;
    }

    private JSONArray mergeGpuSpecs(JSONArray availabilitySpecs, JSONArray catalogSpecs) {
        Map<String, JSONObject> catalogBySpec = new LinkedHashMap<>();
        if (catalogSpecs != null) {
            for (Object item : catalogSpecs) {
                JSONObject spec = toJSONObject(item);
                if (spec != null) {
                    catalogBySpec.put(specKey(spec), spec);
                }
            }
        }
        JSONArray mergedSpecs = new JSONArray();
        if (availabilitySpecs == null) {
            return mergedSpecs;
        }
        for (Object item : availabilitySpecs) {
            JSONObject availableSpec = toJSONObject(item);
            if (availableSpec == null) {
                continue;
            }
            JSONObject catalogSpec = catalogBySpec.get(specKey(availableSpec));
            JSONObject mergedSpec = catalogSpec == null
                    ? JSON.parseObject(availableSpec.toJSONString())
                    : JSON.parseObject(catalogSpec.toJSONString());
            mergedSpec.put("gpuCounts", availableSpec.getJSONArray("gpuCounts") != null
                    ? availableSpec.getJSONArray("gpuCounts") : mergedSpec.getJSONArray("gpuCounts"));
            mergedSpec.put("instanceTypes", mergeInstanceTypes(
                    availableSpec.getJSONArray("instanceTypes"),
                    catalogSpec == null ? null : catalogSpec.getJSONArray("instanceTypes")));
            mergedSpecs.add(mergedSpec);
        }
        return mergedSpecs;
    }

    private JSONArray mergeInstanceTypes(JSONArray availabilityInstances, JSONArray catalogInstances) {
        Map<String, JSONObject> catalogByInstance = new LinkedHashMap<>();
        if (catalogInstances != null) {
            for (Object item : catalogInstances) {
                JSONObject instance = toJSONObject(item);
                if (instance != null) {
                    catalogByInstance.put(getString(instance, "instanceTypeId", "instance_type_id"), instance);
                }
            }
        }
        JSONArray mergedInstances = new JSONArray();
        if (availabilityInstances == null) {
            return mergedInstances;
        }
        for (Object item : availabilityInstances) {
            JSONObject availableInstance = toJSONObject(item);
            if (availableInstance == null) {
                continue;
            }
            String instanceId = getString(availableInstance, "instanceTypeId", "instance_type_id");
            JSONObject catalogInstance = catalogByInstance.get(instanceId);
            JSONObject mergedInstance = catalogInstance == null
                    ? JSON.parseObject(availableInstance.toJSONString())
                    : JSON.parseObject(catalogInstance.toJSONString());
            // availability 中的 null 价格不能覆盖 catalog 返回的价格对象。
            for (Map.Entry<String, Object> entry : availableInstance.entrySet()) {
                if (entry.getValue() != null) {
                    mergedInstance.put(entry.getKey(), entry.getValue());
                }
            }
            mergedInstances.add(mergedInstance);
        }
        return mergedInstances;
    }

    private String specKey(JSONObject spec) {
        return defaultString(getString(spec, "gpuModel", "gpu_model"), "") + "|"
                + defaultString(getString(spec, "gpuMemory", "gpu_memory"), "");
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    private List<GpuClusterVO> fetchClusters(String status) {
        JSONObject result = fetchSchedulerClustersNodes(status);
        JSONArray clusters = extractArray(result, "clusters");
        if (clusters == null) {
            return aggregateClustersFromNodes(extractArray(result, "nodes"));
        }
        List<GpuClusterVO> list = new ArrayList<>();
        for (Object item : clusters) {
            JSONObject cluster = toJSONObject(item);
            if (cluster == null) {
                continue;
            }
            GpuClusterVO vo = new GpuClusterVO();
            vo.setClusterId(defaultString(getString(cluster, "cluster_id", "clusterId"), "--"));
            vo.setClusterName(defaultString(getString(cluster, "cluster_name", "clusterName"), "--"));
            vo.setRegion(defaultString(getString(cluster, "region", "region_name", "regionName", "region_code", "regionCode"), "--"));
            vo.setVersion(defaultString(getString(cluster, "version", "cluster_version", "clusterVersion"), "--"));
            vo.setStatus(defaultString(getString(cluster, "status"), "--"));
            vo.setNodeCount(intValue(cluster, "node_count", "nodeCount"));
            vo.setReadyNodeCount(resolveReadyNodeCount(cluster));
            vo.setGpuTotal(resolveUsageTotal(cluster, "gpu"));
            vo.setGpuAvailable(resolveUsageAvailable(cluster, "gpu"));
            vo.setGpuModels("--");
            vo.setLabels(resolveLabels(cluster));
            vo.setUsageGpu(defaultString(getString(objectValue(cluster, "usage"), "gpu"), "--"));
            vo.setUsageMemoryGi(defaultString(getString(objectValue(cluster, "usage"), "memory_gi", "memoryGi"), "--"));
            vo.setCreateTime(defaultString(getString(cluster, "create_time", "createTime"), "--"));
            vo.setOperations(resolveOperations(cluster));
            list.add(vo);
        }
        return list;
    }

    private List<JSONObject> fetchNodes(String status) {
        JSONObject result = fetchSchedulerClustersNodes(status);
        JSONArray nodes = extractArray(result, "nodes");
        if (nodes == null) {
            JSONArray clusters = extractArray(result, "clusters");
            log.info("GPU scheduler cluster nodes response has no nodes array, resolving from {} clusters", clusters == null ? 0 : clusters.size());
            nodes = fetchNodesFromClusterSummaries(clusters);
        }
        if (nodes == null) {
            log.info("GPU scheduler resolved 0 nodes");
            return List.of();
        }
        log.info("GPU scheduler resolved {} nodes before local filters", nodes.size());
        List<JSONObject> list = new ArrayList<>();
        for (Object item : nodes) {
            JSONObject node = toJSONObject(item);
            if (node != null) {
                list.add(node);
            }
        }
        return list;
    }

    private JSONArray fetchNodesFromClusterSummaries(JSONArray clusters) {
        if (clusters == null || clusters.isEmpty()) {
            return null;
        }
        JSONArray nodes = new JSONArray();
        for (Object item : clusters) {
            JSONObject cluster = toJSONObject(item);
            if (cluster == null) {
                continue;
            }
            String clusterId = getString(cluster, "cluster_id", "clusterId");
            JSONObject summary = getClusterSummary(clusterId);
            JSONArray summaryNodes = extractArray(summary, "nodes");
            log.info("GPU scheduler cluster {} summary resolved {} nodes", clusterId, summaryNodes == null ? 0 : summaryNodes.size());
            if (summaryNodes == null) {
                continue;
            }
            for (Object nodeItem : summaryNodes) {
                JSONObject node = toJSONObject(nodeItem);
                if (node == null) {
                    continue;
                }
                putIfAbsent(node, "cluster_id", getString(summary, "cluster_id", "clusterId", "id"));
                putIfAbsent(node, "cluster_name", getString(summary, "cluster_name", "clusterName", "name"));
                putIfAbsent(node, "region", getString(summary, "region", "region_code", "regionCode"));
                nodes.add(node);
            }
        }
        return nodes;
    }

    private JSONObject fetchSchedulerClustersNodes(String status) {
        String url = buildSchedulerClustersNodesUrl();
        try {
            log.info("Fetching GPU scheduler cluster nodes from {}", url);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class);
            return parseSchedulerNodesResponse(response.getBody());
        } catch (Exception e) {
            log.error("Failed to fetch GPU scheduler clusters from {}", url, e);
            JSONObject empty = new JSONObject();
            empty.put("total", 0);
            empty.put("clusters", new JSONArray());
            empty.put("nodes", new JSONArray());
            return empty;
        }
    }

    private String buildSchedulerClustersNodesUrl() {
        return appendAdminAuthKey(UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v1/admin/clusters/nodes"));
    }

    private String buildSchedulerClusterSummaryUrl(String clusterId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v1/admin/clusters/summary")
                .queryParamIfPresent("cluster_id", StringUtils.isBlank(clusterId)
                        ? java.util.Optional.empty()
                        : java.util.Optional.of(clusterId));
        return appendAdminAuthKey(builder);
    }

    private String buildSchedulerMonitorOverviewUrl() {
        return appendAdminAuthKey(UriComponentsBuilder.fromHttpUrl(baseUrl + "/api/v1/admin/monitor/overview"));
    }

    private String appendAdminAuthKey(UriComponentsBuilder builder) {
        return builder
                .queryParamIfPresent("authKey", StringUtils.isBlank(resolveAdminAuthKey())
                        ? java.util.Optional.empty()
                        : java.util.Optional.of(resolveAdminAuthKey()))
                .toUriString();
    }

    private String resolveAdminAuthKey() {
        return StringUtils.defaultIfBlank(adminAuthKey, schedulerAuthKey);
    }

    private JSONObject parseSchedulerNodesResponse(String body) {
        Object parsed = JSON.parse(body);
        if (parsed instanceof JSONArray array) {
            JSONObject result = new JSONObject();
            result.put("nodes", array);
            return result;
        }
        if (parsed instanceof JSONObject object) {
            return object;
        }
        return new JSONObject();
    }

    private JSONArray extractArray(JSONObject object, String key) {
        if (object == null) {
            return null;
        }
        JSONArray direct = object.getJSONArray(key);
        if (direct != null) {
            return direct;
        }
        JSONObject data = object.getJSONObject("data");
        if (data != null) {
            JSONArray nested = data.getJSONArray(key);
            if (nested != null) {
                return nested;
            }
        }
        JSONObject result = object.getJSONObject("result");
        if (result != null) {
            return result.getJSONArray(key);
        }
        return null;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        GpuPodTenantProvider.Tenant tenant = tenantProvider.getCurrentTenant();
        String token = tokenManager.getAccessToken(tenant.tenantId(), tenant.tenantName());
        headers.setBearerAuth(token);
        return headers;
    }

    private HttpHeaders createAdminHeaders() {
        return new HttpHeaders();
    }

    private List<GpuClusterVO> aggregateClustersFromNodes(JSONArray nodes) {
        if (nodes == null) {
            return List.of();
        }
        Map<String, ClusterSummary> summaryMap = new LinkedHashMap<>();
        for (Object item : nodes) {
            JSONObject node = toJSONObject(item);
            if (node == null) {
                continue;
            }
            String clusterName = defaultString(getString(node, "cluster_name", "cluster", "clusterName"), "默认集群");
            ClusterSummary summary = summaryMap.computeIfAbsent(clusterName, name -> {
                ClusterSummary value = new ClusterSummary();
                value.clusterName = name;
                value.region = defaultString(getString(node, "region", "region_name", "regionName", "zone"), "--");
                value.version = defaultString(getString(node, "cluster_version", "clusterVersion", "kubernetes_version", "kubernetesVersion", "kubelet_version", "kubeletVersion"), "--");
                value.status = "Ready";
                return value;
            });
            summary.nodeCount += 1;
            if ("Ready".equalsIgnoreCase(getString(node, "status"))) {
                summary.readyNodeCount += 1;
            } else {
                summary.status = "异常";
            }
            summary.gpuTotal += intValue(node, "gpu_count", "gpuCount");
            summary.gpuAvailable += intValue(node, "available_gpus", "availableGpus");
            String gpuModel = getString(node, "gpu_model", "gpuModel");
            if (StringUtils.isNotBlank(gpuModel)) {
                summary.gpuModels.add(gpuModel);
            }
        }
        return summaryMap.values().stream().map(summary -> {
            GpuClusterVO vo = new GpuClusterVO();
            vo.setClusterId(summary.clusterName);
            vo.setClusterName(summary.clusterName);
            vo.setRegion(summary.region);
            vo.setVersion(summary.version);
            vo.setNodeCount(summary.nodeCount);
            vo.setReadyNodeCount(summary.readyNodeCount);
            vo.setGpuTotal(summary.gpuTotal);
            vo.setGpuAvailable(summary.gpuAvailable);
            vo.setGpuModels(summary.gpuModels.isEmpty() ? "--" : String.join(" / ", summary.gpuModels));
            vo.setLabels("--");
            vo.setUsageGpu((summary.gpuTotal - summary.gpuAvailable) + "/" + summary.gpuTotal);
            vo.setUsageMemoryGi("--");
            vo.setCreateTime("--");
            vo.setOperations(List.of("详情", "应用管理", "更多"));
            vo.setStatus(summary.status);
            return vo;
        }).toList();
    }

    private Integer resolveReadyNodeCount(JSONObject cluster) {
        Integer value = nullableIntValue(cluster, "ready_node_count", "readyNodeCount");
        if (value != null) {
            return value;
        }
        String status = getString(cluster, "status");
        Integer nodeCount = intValue(cluster, "node_count", "nodeCount");
        return "运行中".equals(status) || "Ready".equalsIgnoreCase(status) ? nodeCount : 0;
    }

    private Integer resolveUsageTotal(JSONObject cluster, String usageKey) {
        String usage = getString(objectValue(cluster, "usage"), usageKey);
        if (StringUtils.isBlank(usage) || !usage.contains("/")) {
            return 0;
        }
        return parseInteger(usage.substring(usage.indexOf("/") + 1));
    }

    private Integer resolveUsageAvailable(JSONObject cluster, String usageKey) {
        String usage = getString(objectValue(cluster, "usage"), usageKey);
        if (StringUtils.isBlank(usage) || !usage.contains("/")) {
            return 0;
        }
        int used = parseInteger(usage.substring(0, usage.indexOf("/")));
        int total = parseInteger(usage.substring(usage.indexOf("/") + 1));
        return Math.max(total - used, 0);
    }

    private String resolveLabels(JSONObject cluster) {
        JSONArray labels = cluster.getJSONArray("labels");
        if (labels == null || labels.isEmpty()) {
            return "--";
        }
        List<String> values = new ArrayList<>();
        for (Object item : labels) {
            JSONObject label = toJSONObject(item);
            if (label != null) {
                String key = getString(label, "key");
                String value = getString(label, "value");
                values.add(StringUtils.isBlank(value) ? key : key + "=" + value);
            }
        }
        return values.isEmpty() ? "--" : String.join(" / ", values);
    }

    private List<String> resolveOperations(JSONObject cluster) {
        JSONArray operations = cluster.getJSONArray("operations");
        if (operations == null || operations.isEmpty()) {
            return List.of("详情", "应用管理", "更多");
        }
        List<String> values = new ArrayList<>();
        for (Object operation : operations) {
            if (operation != null && StringUtils.isNotBlank(String.valueOf(operation))) {
                values.add(String.valueOf(operation));
            }
        }
        return values.isEmpty() ? List.of("详情", "应用管理", "更多") : values;
    }

    private <T> PageResult<T> pageList(List<T> list, PageQuery pageQuery) {
        int pageNo = pageQuery == null || pageQuery.getPageNo() <= 0 ? 1 : pageQuery.getPageNo();
        int pageSize = pageQuery == null || pageQuery.getPageSize() <= 0 ? 10 : pageQuery.getPageSize();
        int from = Math.min((pageNo - 1) * pageSize, list.size());
        int to = Math.min(from + pageSize, list.size());
        PageResult<T> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setDataTotal(list.size());
        result.setPageTotal((long) Math.ceil((double) list.size() / pageSize));
        result.setList(list.subList(from, to));
        return result;
    }

    private boolean contains(String source, String keyword) {
        return StringUtils.isBlank(keyword) || StringUtils.contains(defaultString(source, ""), keyword);
    }

    private JSONObject toJSONObject(Object value) {
        if (value instanceof JSONObject) {
            return (JSONObject) value;
        }
        return value == null ? null : JSON.parseObject(JSON.toJSONString(value));
    }

    private JSONObject objectValue(JSONObject object, String key) {
        return object == null ? null : object.getJSONObject(key);
    }

    private void putIfAbsent(JSONObject object, String key, String value) {
        if (object != null && StringUtils.isNotBlank(value) && !object.containsKey(key)) {
            object.put(key, value);
        }
    }

    private String getString(JSONObject object, String... keys) {
        if (object == null) {
            return null;
        }
        for (String key : keys) {
            String value = object.getString(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private Integer nullableIntValue(JSONObject object, String... keys) {
        if (object == null) {
            return null;
        }
        for (String key : keys) {
            Object value = object.get(key);
            if (value != null) {
                return parseInteger(String.valueOf(value));
            }
        }
        return null;
    }

    private int intValue(JSONObject object, String... keys) {
        Integer value = nullableIntValue(object, keys);
        return value == null ? 0 : value;
    }

    private BigDecimal decimalValue(JSONObject object, String... keys) {
        if (object == null) {
            return BigDecimal.ZERO;
        }
        for (String key : keys) {
            Object value = object.get(key);
            if (value != null) {
                try {
                    return new BigDecimal(String.valueOf(value));
                } catch (NumberFormatException ignored) {
                    return BigDecimal.ZERO;
                }
            }
        }
        return BigDecimal.ZERO;
    }

    private int parseInteger(String value) {
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        try {
            return new BigDecimal(value.trim()).intValue();
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String defaultString(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private static class ClusterSummary {
        private String clusterName;
        private String region;
        private String version;
        private int nodeCount;
        private int readyNodeCount;
        private int gpuTotal;
        private int gpuAvailable;
        private String status;
        private final Set<String> gpuModels = new LinkedHashSet<>();
    }
}
