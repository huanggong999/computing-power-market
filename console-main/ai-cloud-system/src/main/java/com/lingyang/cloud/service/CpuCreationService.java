package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.GpuPodTenantProvider;
import com.lingyang.cloud.client.InstanceResourceAdapterClient;
import jakarta.annotation.PreDestroy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

/** Durable at-most-once CPU purchase queue. Uncertain submissions are never automatically retried. */
@Service
public class CpuCreationService {
    private static final Logger log = LoggerFactory.getLogger(CpuCreationService.class);
    private final JdbcTemplate jdbc;
    private final GpuPodTenantProvider tenants;
    private final InstanceResourceAdapterClient client;
    private final VolcanoCpuSalePriceService prices;
    private final CpuPurchaseOrderService orders;
    private final TransactionTemplate writeTransaction;
    private final ThreadPoolExecutor workers = new ThreadPoolExecutor(0, 4, 60, TimeUnit.SECONDS,
            new SynchronousQueue<>(), runnable -> {
                Thread thread = new Thread(runnable, "cpu-creation");
                thread.setDaemon(true);
                return thread;
            });

    public CpuCreationService(JdbcTemplate jdbc, GpuPodTenantProvider tenants, InstanceResourceAdapterClient client,
                              VolcanoCpuSalePriceService prices, CpuPurchaseOrderService orders,
                              PlatformTransactionManager transactionManager) {
        this.jdbc = jdbc;
        this.tenants = tenants;
        this.client = client;
        this.prices = prices;
        this.orders = orders;
        this.writeTransaction = new TransactionTemplate(transactionManager);
        this.writeTransaction.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject submit(JSONObject body) {
        var tenant = tenants.getCurrentTenant();
        String key = body.getString("requestKey");
        if (key == null || !key.matches("[A-Za-z0-9_-]{16,128}")) throw new IllegalArgumentException("缺少有效购买请求编号，请刷新后重试");
        JSONObject request = purchaseFields(body);
        List<Map<String, Object>> existing = jdbc.queryForList("SELECT * FROM cpu_creation_operation WHERE tenant_id=? AND request_key=?", tenant.tenantId(), key);
        if (!existing.isEmpty()) return existingRequest(existing.get(0), request);
        validate(request);
        String id = UUID.randomUUID().toString();
        try {
            jdbc.update("INSERT INTO cpu_creation_operation(operation_id,tenant_id,tenant_name,request_key,request_json,status,message) VALUES(?,?,?,?,?,'pending',?)",
                    id, tenant.tenantId(), tenant.tenantName(), key, request.toJSONString(), "已受理，CPU 实例创建中，可能需要几分钟");
        } catch (DuplicateKeyException duplicate) {
            return existingRequest(jdbc.queryForMap("SELECT * FROM cpu_creation_operation WHERE tenant_id=? AND request_key=? FOR UPDATE", tenant.tenantId(), key), request);
        }
        // Ignore browser pricing. Quote after the idempotency check, so retries keep the original price.
        JSONObject pricing = prices.quote(request);
        Long orderId = orders.create(id, request, pricing);
        jdbc.update("UPDATE cpu_creation_operation SET pricing_json=?,order_id=? WHERE operation_id=?", pricing.toJSONString(), orderId, id);
        dispatchAfterCommit(id);
        return get(id);
    }

    /** Submit exactly once after the purchase transaction commits. Pending rows are never replayed by a timer. */
    private void dispatchAfterCommit(String operationId) {
        Runnable task = () -> {
            try {
                workers.execute(() -> {
                    try {
                        execute(jdbc.queryForMap("SELECT * FROM cpu_creation_operation WHERE operation_id=?", operationId));
                    } catch (Exception error) {
                        log.error("CPU creation stopped; no automatic resubmission. operation={}", operationId, error);
                    }
                });
            } catch (RejectedExecutionException busy) {
                // The operation remains visible as pending for manual reconciliation; never resubmit automatically.
                log.warn("CPU creation worker busy; operation requires manual reconciliation: {}", operationId);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCommit() { task.run(); }
            });
        } else {
            task.run();
        }
    }

    private JSONObject existingRequest(Map<String, Object> row, JSONObject request) {
        if (!JSON.parseObject((String) row.get("request_json")).equals(request)) throw new IllegalArgumentException("请求编号已用于其他配置，请先确认原购买结果");
        return response(row);
    }

    public JSONObject get(String id) {
        var rows = jdbc.queryForList("SELECT * FROM cpu_creation_operation WHERE operation_id=? AND tenant_id=?", id, tenants.getCurrentTenant().tenantId());
        if (rows.isEmpty()) throw new IllegalArgumentException("创建任务不存在");
        return response(rows.get(0));
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject clearFailedOperation(String id) {
        var tenant = tenants.getCurrentTenant();
        Map<String, Object> row;
        try {
            row = jdbc.queryForMap("SELECT * FROM cpu_creation_operation WHERE operation_id=? AND tenant_id=?", id, tenant.tenantId());
        } catch (org.springframework.dao.EmptyResultDataAccessException missing) {
            throw new IllegalArgumentException("异常记录不存在或已清除");
        }
        String status = String.valueOf(row.get("status"));
        if (!"failed".equals(status)) {
            throw new IllegalArgumentException("只有创建失败的 CPU 记录可以清除");
        }
        if ("pending".equals(String.valueOf(row.get("settlement_status")))) {
            orders.settle(id);
            row = jdbc.queryForMap("SELECT * FROM cpu_creation_operation WHERE operation_id=? AND tenant_id=?", id, tenant.tenantId());
        }
        if (!"done".equals(String.valueOf(row.get("settlement_status")))) {
            throw new IllegalStateException("订单结算尚未完成，请稍后重试清除");
        }
        jdbc.update("DELETE FROM cpu_creation_operation WHERE operation_id=? AND tenant_id=?", id, tenant.tenantId());
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("message", "异常记录已清除");
        return result;
    }

    public JSONObject mergeList(JSONObject upstream) {
        // Preserve known operations even when the upstream list temporarily fails.
        JSONArray items = new JSONArray();
        JSONArray source = upstream.getJSONArray("instances");
        if (source != null) for (Object value : source) {
            JSONObject item = JSON.parseObject(JSON.toJSONString(value));
            if ("cpu_instance".equals(item.getString("origin"))) items.add(item);
        }
        Set<String> ids = new HashSet<>();
        for (Object item : items) ids.add(((JSONObject) item).getString("instance_id"));
        for (var row : jdbc.queryForList("SELECT * FROM cpu_creation_operation WHERE tenant_id=? ORDER BY created_at DESC", tenants.getCurrentTenant().tenantId())) {
            JSONObject operation = response(row);
            JSONArray instances = operation.getJSONArray("instances");
            if (instances != null && !instances.isEmpty()) {
                for (Object value : instances) {
                    JSONObject instance = JSON.parseObject(JSON.toJSONString(value));
                    String id = instance.getString("instance_id");
                    // Terminal upstream list remains authoritative, including release/removal.
                    if (id != null && !ids.contains(id) && !"succeeded".equals(row.get("status"))) {
                        instance.put("origin", "cpu_instance");
                        instance.put("operation_id", row.get("operation_id"));
                        items.add(instance);
                        ids.add(id);
                    }
                }
            } else items.add(placeholder(row));
        }
        JSONObject result = new JSONObject(upstream);
        result.put("instances", items);
        result.put("total", items.size());
        if (Boolean.FALSE.equals(upstream.getBoolean("success"))) {
            result.put("list_warning", upstream.getString("message"));
            result.put("success", true);
        }
        return result;
    }

    @Scheduled(fixedDelay = 2000, initialDelay = 5000)
    public void dispatch() {
        for (var row : jdbc.queryForList("SELECT operation_id FROM cpu_creation_operation WHERE settlement_status='pending' ORDER BY updated_at LIMIT 20")) {
            try { orders.settle((String)row.get("operation_id")); }
            catch (Exception pendingSettlement) { /* Persisted cloud result will be settled next tick, without resubmitting. */ }
        }
    }

    void execute(Map<String, Object> row) {
        String id = (String) row.get("operation_id");
        // Hikari has auto-commit=false. Commit the claim before any cloud call, even across processes.
        Integer claimed = writeTransaction.execute(tx -> jdbc.update("UPDATE cpu_creation_operation SET status='submitting',updated_at=CURRENT_TIMESTAMP WHERE operation_id=? AND status='pending'", id));
        if (!Integer.valueOf(1).equals(claimed)) return;
        JSONObject result;
        String status;
        try {
            JSONObject request = JSON.parseObject((String)row.get("request_json"));
            if (row.get("pricing_json") != null) {
                JSONObject pricing = JSON.parseObject((String)row.get("pricing_json"));
                JSONObject upstreamPricing = new JSONObject();
                upstreamPricing.put("unit_price", pricing.get("upstreamPrice"));
                upstreamPricing.put("discount_unit_price", pricing.get("upstreamPrice"));
                request.put("pricing", upstreamPricing);
            }
            result = client.purchaseCpuForTenant(request, (String) row.get("tenant_id"), (String) row.get("tenant_name"));
            JSONArray instances = result.getJSONArray("instances");
            status = Boolean.FALSE.equals(result.getBoolean("success")) ? (instances == null || instances.isEmpty() ? "failed" : "partial") : "succeeded";
            if ("succeeded".equals(status) && (instances == null || instances.isEmpty())) status = "unknown";
        } catch (Exception error) {
            // The cloud may have accepted the call. Neither retry nor mark creation failed.
            status = "unknown";
            result = new JSONObject();
            result.put("message", "请求状态未知，请确认 CPU 实例列表；为避免重复购买，系统不会自动重试");
        }
        String finalStatus = status;
        JSONObject finalResult = result;
        writeTransaction.executeWithoutResult(tx -> jdbc.update("UPDATE cpu_creation_operation SET status=?,result_json=?,message=?,settlement_status='pending',updated_at=CURRENT_TIMESTAMP WHERE operation_id=? AND status='submitting'", finalStatus, finalResult.toJSONString(), finalResult.getString("message"), id));
        try { orders.settle(id); } catch (Exception pendingSettlement) { /* Retry local settlement, never the cloud call. */ }
    }

    static JSONObject response(Map<String, Object> row) {
        String payload = (String) row.get("result_json");
        JSONObject response = payload == null ? new JSONObject() : JSON.parseObject(payload);
        String status = (String) row.get("status");
        response.put("operation_id", row.get("operation_id"));
        response.put("operation_status", status);
        response.put("accepted", true);
        if (row.get("pricing_json") != null) response.put("pricing", JSON.parseObject((String)row.get("pricing_json")));
        if (row.get("order_id") != null) response.put("order_id", String.valueOf(row.get("order_id")));
        response.put("success", !"failed".equals(status));
        if (response.getString("message") == null) response.put("message", row.get("message"));
        if (response.getString("message") == null) response.put("message", "创建中，请稍后刷新查看");
        return response;
    }

    static JSONObject placeholder(Map<String, Object> row) {
        JSONObject item = JSON.parseObject((String) row.get("request_json"));
        String status = (String) row.get("status");
        item.put("id", row.get("operation_id"));
        item.put("operation_id", row.get("operation_id"));
        item.put("instance_name", "CPU 创建任务（" + item.getInteger("count") + " 台）");
        item.put("origin", "cpu_instance");
        item.put("status", "failed".equals(status) ? "failed" : "creating");
        item.put("status_message", "unknown".equals(status) || "submitting".equals(status)
                ? "云端创建中或请求状态待确认，请稍后刷新；请勿重复购买" : row.get("message"));
        item.put("create_time", String.valueOf(row.get("created_at")));
        JSONObject result = row.get("result_json") == null ? new JSONObject() : JSON.parseObject((String) row.get("result_json"));
        item.put("checks", result.get("checks"));
        item.put("errors", result.get("errors"));
        return item;
    }

    static JSONObject purchaseFields(JSONObject body) {
        JSONObject request = new JSONObject();
        for (String field : List.of("region", "zone", "instanceTypeId", "billingMode", "count", "systemDiskGb", "dataDiskGb")) request.put(field, body.get(field));
        if (!"on_demand".equals(body.getString("billingMode"))) request.put("duration", body.get("duration"));
        return request;
    }

    private void validate(JSONObject body) {
        for (String field : List.of("region", "zone", "instanceTypeId")) if (body.getString(field) == null || body.getString(field).isBlank()) throw new IllegalArgumentException("请完整选择地域、可用区和 CPU 规格");
        if (!List.of("on_demand", "monthly", "yearly").contains(body.getString("billingMode"))) throw new IllegalArgumentException("计费方式无效");
        integer(body, "systemDiskGb", 20, 2048);
        integer(body, "dataDiskGb", 0, 8192);
        integer(body, "count", 1, 10);
        if (!"on_demand".equals(body.getString("billingMode"))) integer(body, "duration", 1, 36);
    }

    static void integer(JSONObject body, String field, int min, int max) {
        Object value = body.get(field);
        try {
            int number = new java.math.BigDecimal(String.valueOf(value)).intValueExact();
            if (number < min || number > max) throw new ArithmeticException();
        } catch (NumberFormatException | ArithmeticException error) { throw new IllegalArgumentException(field + " 必须为 " + min + "–" + max + " 的整数"); }
    }

    @PreDestroy
    public void close() { workers.shutdown(); }
}
