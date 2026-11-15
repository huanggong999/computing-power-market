package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.entity.*;
import com.lingyang.cloud.enums.order.OrderStatusEnum;
import com.lingyang.cloud.mapper.*;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

/** Verifies settlement against a persisted operation and real balance updates without any cloud calls. */
public class CpuPurchaseOrderServiceTest {
    private JdbcTemplate jdbc;
    private CpuPurchaseOrderService service;
    private SysOrderEntity order;
    private SysOrderSourceEntity source;
    private final List<SysCustomerAmountLogEntity> logs = new ArrayList<>();

    @Before public void setup() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        jdbc = new JdbcTemplate(ds);
        new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V2026_10_22_001__cpu_creation_operation.sql"),
                new ClassPathResource("db/migration/V2026_10_22_004__cpu_creation_pricing.sql")).execute(ds);
        jdbc.execute("CREATE TABLE sys_customer(id BIGINT PRIMARY KEY,balance DECIMAL(12,2))");
        jdbc.update("INSERT INTO sys_customer VALUES(1,0)");
        order = new SysOrderEntity(); order.setId(10L); order.setOrderNo("CPU10"); order.setCreateById(1L); order.setOrderStatus(OrderStatusEnum.PAID);
        source = new SysOrderSourceEntity(); source.setConfigDetail(new JSONObject());
        service = new CpuPurchaseOrderService();
        field("jdbc", jdbc);
        field("orders", Proxy.newProxyInstance(SysOrderMapper.class.getClassLoader(), new Class<?>[]{SysOrderMapper.class}, (p, m, args) -> {
            if (m.getName().equals("selectById")) return order;
            if (m.getName().equals("updateById")) { order = (SysOrderEntity) args[0]; return 1; }
            throw new AssertionError(m.getName());
        }));
        field("sources", Proxy.newProxyInstance(SysOrderSourceMapper.class.getClassLoader(), new Class<?>[]{SysOrderSourceMapper.class}, (p, m, args) -> {
            if (m.getName().equals("selectList")) return List.of(source);
            if (m.getName().equals("updateById")) { source = (SysOrderSourceEntity) args[0]; return 1; }
            throw new AssertionError(m.getName());
        }));
        field("balanceLogs", Proxy.newProxyInstance(SysCustomerAmountLogMapper.class.getClassLoader(), new Class<?>[]{SysCustomerAmountLogMapper.class}, (p, m, args) -> {
            if (m.getName().equals("insert")) { logs.add((SysCustomerAmountLogEntity) args[0]); return 1; }
            throw new AssertionError(m.getName());
        }));
    }

    @After public void cleanup() { jdbc.execute("SHUTDOWN"); }

    @Test public void failureRefundsExactlyOnce() {
        operation("failed", "{\"success\":false}");
        service.settle("op"); service.settle("op");
        assertEquals(new BigDecimal("1200.00"), balance());
        assertEquals(1, logs.size());
        assertEquals(OrderStatusEnum.REFUNDED, order.getOrderStatus());
        assertEquals("done", settlement());
    }

    @Test public void partialSuccessRefundsOnlyMissingInstances() {
        operation("partial", "{\"success\":false,\"instances\":[{\"instance_id\":\"cpu-1\"}]}");
        service.settle("op"); service.settle("op");
        assertEquals(new BigDecimal("800.00"), balance());
        assertEquals(1, logs.size());
        assertEquals(1, source.getConfigDetail().getIntValue("createdCount"));
        assertEquals(OrderStatusEnum.PAID, order.getOrderStatus());
    }

    @Test public void unknownOrInconsistentSuccessRequiresReconciliationWithoutRefund() {
        operation("unknown", "{}");
        service.settle("op");
        assertEquals("manual", settlement()); assertTrue(logs.isEmpty());
        jdbc.update("DELETE FROM cpu_creation_operation");
        operation("succeeded", "{\"success\":true,\"instances\":[{\"instance_id\":\"cpu-1\"}]}");
        service.settle("op");
        assertEquals("manual", settlement());
        assertEquals("unknown", jdbc.queryForObject("SELECT status FROM cpu_creation_operation", String.class));
        assertEquals(new BigDecimal("0.00"), balance()); assertTrue(logs.isEmpty());
    }

    @Test public void completeSuccessDoesNotRefundAndRecordsCreationResult() {
        operation("succeeded", "{\"success\":true,\"instances\":[{\"instance_id\":\"cpu-1\"},{\"instance_id\":\"cpu-2\"},{\"instance_id\":\"cpu-3\"}]}");
        service.settle("op");
        assertEquals("done", settlement()); assertTrue(logs.isEmpty());
        assertEquals(3, source.getConfigDetail().getIntValue("createdCount"));
        assertNotNull(source.getConfigDetail().getJSONObject("creationResult"));
    }

    private void operation(String status, String result) {
        JSONObject pricing = JSON.parseObject("{\"total_cost\":1200,\"salePrice\":200,\"quantity\":3,\"billingPeriods\":2}");
        jdbc.update("INSERT INTO cpu_creation_operation(operation_id,tenant_id,tenant_name,request_key,request_json,status,result_json,pricing_json,order_id,settlement_status) VALUES('op','1','user','request','{}',?,?,?,10,'pending')", status, result, pricing.toJSONString());
    }
    private BigDecimal balance() { return jdbc.queryForObject("SELECT balance FROM sys_customer WHERE id=1", BigDecimal.class); }
    private String settlement() { return jdbc.queryForObject("SELECT settlement_status FROM cpu_creation_operation", String.class); }
    private void field(String name, Object value) throws Exception { var field = CpuPurchaseOrderService.class.getDeclaredField(name); field.setAccessible(true); field.set(service, value); }
}
