package com.lingyang.cloud.client;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.config.RestTemplateConfig;
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

    @Value("${gpu.scheduler.connect-timeout:5000}")
    private Integer connectTimeout;

    @Value("${gpu.scheduler.read-timeout:10000}")
    private Integer readTimeout;

    private RestTemplate restTemplate;

    @Resource
    private GpuPodTokenManager tokenManager;

    @Resource
    private GpuPodTenantProvider tenantProvider;

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
