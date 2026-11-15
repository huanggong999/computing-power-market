package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.customer.SysCustomerAmountType;
import com.lingyang.cloud.enums.customer.SysTransactionType;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.enums.order.OrderTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeTypeEnum;
import com.lingyang.cloud.enums.source.SourceChargeUnitEnum;
import com.lingyang.cloud.enums.source.SourceTypeEnum;
import com.lingyang.cloud.mapper.*;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.IdUtils;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/** Local order, payment and creation queue share one transaction; cloud calls never hold DB locks. */
@Service
public class CpuPurchaseOrderService {
    @Resource private JdbcTemplate jdbc;
    @Resource private SysOrderMapper orders;
    @Resource private SysOrderSourceMapper sources;
    @Resource private SysCustomerAmountLogMapper balanceLogs;

    @Transactional(rollbackFor = Exception.class)
    public Long create(String operationId, JSONObject request, JSONObject pricing) {
        var user = SecurityContext.getUserInfo();
        Long userId = user.getUserId();
        String username = user.getUsername();
        Long id = IdUtils.nextId();
        String orderNo = "CPU" + id;
        Date now = new Date();
        BigDecimal total = pricing.getBigDecimal("total_cost");
        changeBalance(id, orderNo, userId, total, false);
        SysOrderEntity order = new SysOrderEntity();
        order.setId(id); order.setOrderNo(orderNo); order.setOrderType(OrderTypeEnum.NEW_RESOURCE);
        order.setOriginalPrice(total); order.setPremiumPrice(pricing.getBigDecimal("premiumAmount"));
        order.setFinalPayAmount(total); order.setBalancePayAmount(total);
        order.setUserDiscountAmount(BigDecimal.ZERO); order.setOnlinePayAmount(BigDecimal.ZERO);
        order.setCouponAmount(BigDecimal.ZERO); order.setVoucherAmount(BigDecimal.ZERO);
        order.setOrderStatus(OrderStatusEnum.PAID); order.setPayTime(now);
        order.setCreateById(userId); order.setCreateBy(username); order.setUpdateById(userId); order.setUpdateBy(username); order.setDelFlag(0);
        orders.insert(order);

        JSONObject config = JSON.parseObject(request.toJSONString());
        config.put("pricing", pricing); config.put("operationId", operationId); config.put("origin", "cpu_instance");
        SysOrderSourceEntity source = new SysOrderSourceEntity();
        source.setOrderId(id); source.setOrderNo(orderNo); source.setSourceId(operationId); source.setUid(UUID.randomUUID().toString());
        source.setSourceType(SourceTypeEnum.ECS); source.setSourceName("CPU 实例 - " + request.getString("instanceTypeId"));
        source.setProductName("火山云 CPU - " + request.getString("instanceTypeId")); source.setProductType(2);
        source.setConfigDetail(config);
        String mode = request.getString("billingMode");
        source.setChargeType("on_demand".equals(mode) ? SourceChargeTypeEnum.POSTPAID_BY_HOUR
                : "yearly".equals(mode) ? SourceChargeTypeEnum.POSTPAID_BY_YEAR : SourceChargeTypeEnum.POSTPAID_BY_MONTH);
        source.setDuration(pricing.getIntValue("duration"));
        source.setDurationUnit("on_demand".equals(mode) ? SourceChargeUnitEnum.HOUR
                : "yearly".equals(mode) ? SourceChargeUnitEnum.YEAR : SourceChargeUnitEnum.MONTH);
        source.setNumber(pricing.getIntValue("quantity"));
        source.setUnitPrice(pricing.getBigDecimal("upstreamPrice"));
        source.setPremiumPrice(pricing.getBigDecimal("salePrice")); source.setFinalUnitPrice(pricing.getBigDecimal("salePrice"));
        source.setUserDiscountAmount(BigDecimal.ZERO); source.setCouponDiscountAmount(BigDecimal.ZERO); source.setVoucherDiscountAmount(BigDecimal.ZERO);
        source.setCreateById(userId); source.setCreateBy(username); source.setUpdateById(userId); source.setUpdateBy(username); source.setDelFlag(0);
        sources.insert(source);
        return id;
    }

    /** Settlement is retryable from persisted cloud results, and cannot call the cloud again. */
    @Transactional(rollbackFor = Exception.class)
    public void settle(String operationId) {
        Map<String, Object> operation = jdbc.queryForMap("SELECT * FROM cpu_creation_operation WHERE operation_id=? FOR UPDATE", operationId);
        if (!"pending".equals(operation.get("settlement_status"))) return;
        JSONObject result = JSON.parseObject((String)operation.get("result_json"));
        String status = (String)operation.get("status");
        Object orderId = operation.get("order_id");
        if (orderId == null) {
            jdbc.update("UPDATE cpu_creation_operation SET settlement_status='done' WHERE operation_id=?", operationId);
            return;
        }
        // An uncertain result is left charged pending reconciliation, never refunded or resubmitted automatically.
        if ("unknown".equals(status)) {
            jdbc.update("UPDATE cpu_creation_operation SET settlement_status='manual' WHERE operation_id=?", operationId);
            return;
        }
        Long id = ((Number)orderId).longValue();
        SysOrderEntity order = orders.selectById(id);
        JSONObject pricing = JSON.parseObject((String)operation.get("pricing_json"));
        JSONArray instances = result.getJSONArray("instances");
        int count = pricing.getIntValue("quantity");
        int created = successfulCount(instances);
        if ("succeeded".equals(status) && created != count) {
            jdbc.update("UPDATE cpu_creation_operation SET status='unknown',settlement_status='manual',message=? WHERE operation_id=?", "返回实例数量不一致，请核实创建结果", operationId);
            return;
        }
        BigDecimal refund = refundAmount(pricing, "failed".equals(status) ? 0 : Math.min(created, count));
        if (refund.signum() > 0) changeBalance(id, order.getOrderNo(), order.getCreateById(), refund, true);
        if (refund.compareTo(pricing.getBigDecimal("total_cost")) == 0) {
            order.setOrderStatus(OrderStatusEnum.REFUNDED); orders.updateById(order);
        }
        for (SysOrderSourceEntity source : sources.selectList(com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(SysOrderSourceEntity.class)
                .eq(SysOrderSourceEntity::getOrderId, id))) {
            JSONObject config = source.getConfigDetail();
            config.put("creationResult", result); config.put("refundAmount", refund); config.put("createdCount", created);
            source.setConfigDetail(config); sources.updateById(source);
        }
        jdbc.update("UPDATE cpu_creation_operation SET settlement_status='done',updated_at=CURRENT_TIMESTAMP WHERE operation_id=?", operationId);
    }

    static int successfulCount(JSONArray instances) {
        if (instances == null) return 0;
        Set<String> ids = new HashSet<>();
        for (Object item : instances) {
            if (item instanceof JSONObject instance) {
                String id = instance.getString("instance_id");
                if (id != null && !id.isBlank()) ids.add(id);
            }
        }
        return ids.size();
    }

    static BigDecimal refundAmount(JSONObject pricing, int created) {
        BigDecimal total = pricing.getBigDecimal("total_cost");
        BigDecimal retained = pricing.getBigDecimal("salePrice").multiply(BigDecimal.valueOf((long)created * pricing.getIntValue("billingPeriods")))
                .setScale(2, RoundingMode.HALF_UP);
        return total.subtract(retained).max(BigDecimal.ZERO);
    }

    private void changeBalance(Long orderId, String orderNo, Long userId, BigDecimal amount, boolean refund) {
        if (amount.signum() <= 0) return;
        List<BigDecimal> balances = jdbc.queryForList("SELECT balance FROM sys_customer WHERE id=? FOR UPDATE", BigDecimal.class, userId);
        BigDecimal before = balances.isEmpty() || balances.get(0) == null ? BigDecimal.ZERO : balances.get(0);
        if (!refund && before.compareTo(amount) < 0) throw new HttpServiceException("余额不足");
        if (balances.isEmpty()) throw new HttpServiceException("客户余额账户不存在");
        BigDecimal after = refund ? before.add(amount) : before.subtract(amount);
        jdbc.update("UPDATE sys_customer SET balance=? WHERE id=?", after, userId);
        SysCustomerAmountLogEntity log = new SysCustomerAmountLogEntity();
        log.setOrderId(orderId); log.setOrderNo(orderNo); log.setCustomerId(userId); log.setAmountType(SysCustomerAmountType.BALANCE);
        log.setTransactionType(refund ? SysTransactionType.REFUND : SysTransactionType.PAY_DISCOUNT);
        log.setBeforeAmount(before); log.setAmount(amount); log.setAfterAmount(after); log.setCreateTime(new Date());
        balanceLogs.insert(log);
    }
}
