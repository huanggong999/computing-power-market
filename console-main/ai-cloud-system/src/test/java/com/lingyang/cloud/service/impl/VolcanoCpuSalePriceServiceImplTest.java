package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.lingyang.cloud.client.InstanceResourceAdapterClient;
import com.lingyang.cloud.api.controller.pc.CpuInstanceAdapterController;
import com.lingyang.cloud.api.controller.system.SystemGpuResourceController;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.cloud.mapper.VolcanoCpuSalePriceMapper;
import com.lingyang.cloud.model.edit.gpu.VolcanoCpuSalePriceEdit;
import com.lingyang.common.core.exception.http.HttpServiceException;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.authentication.MultipleSourceAuthenticationToken;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.*;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.*;
import static org.junit.Assert.*;

public class VolcanoCpuSalePriceServiceImplTest {
    private VolcanoCpuSalePriceServiceImpl service;
    private VolcanoCpuSalePriceMapper mapper;
    private JdbcTemplate jdbc;
    private FakeClient client;

    @Before public void setup() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE");
        jdbc = new JdbcTemplate(ds);
        new ResourceDatabasePopulator(
                new ClassPathResource("db/migration/V2026_10_22_001__cpu_creation_operation.sql"),
                new ClassPathResource("db/migration/V2026_10_22_002__volcano_cpu_sale_price.sql"),
                new ClassPathResource("db/migration/V2026_10_22_003__volcano_cpu_catalog_snapshot.sql"),
                new ClassPathResource("db/migration/V2026_10_22_004__cpu_creation_pricing.sql")).execute(ds);
        MybatisSqlSessionFactoryBean factory = new MybatisSqlSessionFactoryBean();
        factory.setDataSource(ds);
        MybatisConfiguration config = new MybatisConfiguration(); config.setMapUnderscoreToCamelCase(true);
        factory.setConfiguration(config);
        factory.setMapperLocations(new ClassPathResource("mapper/VolcanoCpuSalePriceMapper.xml"));
        mapper = new SqlSessionTemplate(factory.getObject()).getMapper(VolcanoCpuSalePriceMapper.class);
        client = new FakeClient();
        service = new VolcanoCpuSalePriceServiceImpl(mapper, jdbc, new TransactionTemplate(new DataSourceTransactionManager(ds)), client);
        LoginUserInfoDetail user = new LoginUserInfoDetail(); user.setUserId(1L); user.setUsername("admin");
        SecurityContext.setAuthentication(MultipleSourceAuthenticationToken.authenticated(user));
        client.catalogs.put("cn-a", catalog("cn-a", "0.28", "128"));
        client.catalogs.put("cn-b", catalog("cn-b", "0.30", "130"));
    }
    @After public void cleanup() { SecurityContext.clear(); jdbc.execute("SHUTDOWN"); }

    @Test public void refreshPersistsSuccessfulRegionsAndPreservesFailedRegion() {
        assertEquals(2, service.refreshCatalog().getIntValue("succeeded"));
        client.catalogs.put("cn-a", catalog("cn-a", "0.40", "150")); client.failing.add("cn-b");
        JSONObject summary = service.refreshCatalog();
        assertEquals(1, summary.getIntValue("succeeded")); assertEquals(1, summary.getIntValue("failed"));
        assertEquals(new BigDecimal("0.40"), displayed("cn-a").getJSONObject("price").getBigDecimal("unitPrice"));
        assertEquals(new BigDecimal("0.30"), displayed("cn-b").getJSONObject("price").getBigDecimal("unitPrice"));
        JSONObject failed = service.getStoredCatalog().getJSONArray("regions").getJSONObject(1).getJSONObject("lastRefreshResult");
        assertFalse(failed.getBooleanValue("success"));
    }

    @Test public void raisesPriceOnUpstreamIncreaseButKeepsPlatformPriceAfterDecrease() {
        service.refreshCatalog(); service.saveOrUpdate(edit("on_demand", "0.50"));
        client.catalogs.put("cn-a", catalog("cn-a", "0.60", "128")); service.refreshCatalog();
        assertEquals(0, new BigDecimal("0.60").compareTo(mapper.selectByUniqueKey("cn-a", "ecs.cpu", "on_demand").getSalePrice()));
        client.catalogs.put("cn-a", catalog("cn-a", "0.20", "128")); service.refreshCatalog();
        assertEquals(0, new BigDecimal("0.60").compareTo(displayed("cn-a").getBigDecimal("salePrice")));
        assertEquals(1, mapper.selectByUniqueKey("cn-a", "ecs.cpu", "on_demand").getStatus().intValue());
    }

    @Test public void rejectsUnderCostPriceAndIgnoresBrowserUpstream() {
        service.refreshCatalog();
        VolcanoCpuSalePriceEdit edit = edit("monthly", "10"); edit.setUpstreamPrice(BigDecimal.ONE);
        try { service.saveOrUpdate(edit); fail(); } catch (HttpServiceException e) { assertTrue(e.getMessage().contains("不能低于")); }
        assertNull(mapper.selectByUniqueKey("cn-a", "ecs.cpu", "monthly"));
    }

    @Test public void disableAndRestoreFallBackAndKeepAdminMetadataOutOfUserCatalog() {
        service.refreshCatalog(); service.saveOrUpdate(edit("monthly", "200"));
        var config = mapper.selectByUniqueKey("cn-a", "ecs.cpu", "monthly");
        assertFalse(displayed("cn-a").containsKey("monthlyPriceConfig"));
        assertNotNull(service.getStoredCatalog().getJSONArray("regions").getJSONObject(0).getJSONArray("specs").getJSONObject(0).get("monthlyPriceConfig"));
        service.updateStatus(config.getId(), 0);
        assertEquals(0, new BigDecimal("128").compareTo(displayed("cn-a").getBigDecimal("salePriceMonthly")));
        service.updateStatus(config.getId(), 1);
        assertEquals(0, new BigDecimal("200").compareTo(displayed("cn-a").getBigDecimal("salePriceMonthly")));
        service.restoreDefaultPrice(config.getId());
        assertEquals(0, new BigDecimal("128").compareTo(displayed("cn-a").getBigDecimal("salePriceMonthly")));
    }

    @Test public void hidesPendingOrUnavailableModeAndRejectsMissingPrice() {
        service.refreshCatalog();
        JSONObject c = catalog("cn-a", "0.28", "128"); c.getJSONArray("specs").getJSONObject(0).getJSONObject("price").put("pricePending", true);
        client.catalogs.put("cn-a", c); service.refreshCatalog();
        assertTrue(service.getDisplayPrices("cn-a", null, "on_demand").getJSONArray("specs").isEmpty());
        assertEquals(1, service.getDisplayPrices("cn-a", null, "yearly").getJSONArray("specs").size());
        try { service.quote(request("on_demand", 1, 1)); fail(); } catch (HttpServiceException e) { assertTrue(e.getMessage().contains("价格")); }
        assertTrue(service.getDisplayPrices("missing", null, "monthly").getJSONArray("specs").isEmpty());
    }

    @Test public void yearlyUsesMonthlyPriceTimesTwelveAndServerDiscardsBrowserAmounts() {
        service.refreshCatalog(); service.saveOrUpdate(edit("monthly", "200"));
        JSONObject request = request("yearly", 2, 3); request.put("pricing", JSON.parseObject("{\"salePrice\":1,\"total_cost\":1}"));
        JSONObject quote = service.quote(request);
        assertEquals(0, new BigDecimal("14400").compareTo(quote.getBigDecimal("total_cost")));
        assertEquals(0, new BigDecimal("128").compareTo(quote.getBigDecimal("upstreamPrice")));
        assertEquals(24, quote.getIntValue("billingPeriods")); assertEquals("PrePaid", quote.getString("chargeType"));
        assertTrue(quote.getString("priceVersion").contains(":"));
    }

    @Test public void userControllerReturnsEffectivePricesAndHidesUnavailableSpecs() throws Exception {
        service.refreshCatalog(); service.saveOrUpdate(edit("monthly", "200"));
        CpuInstanceAdapterController controller = new CpuInstanceAdapterController();
        var field = CpuInstanceAdapterController.class.getDeclaredField("priceService");
        field.setAccessible(true); field.set(controller, service);
        JSONObject response = controller.prices("cn-a", "ecs.cpu", "yearly").getData();
        JSONObject spec = response.getJSONArray("specs").getJSONObject(0);
        assertEquals(0, new BigDecimal("200").compareTo(spec.getBigDecimal("salePriceMonthly")));
        assertEquals(new BigDecimal("128"), spec.getJSONObject("priceMonthly").getBigDecimal("unitPrice"));
        assertNotNull(spec.getString("priceVersion"));
        assertFalse(spec.containsKey("monthlyPriceConfig"));
        assertTrue(controller.prices("cn-a", "unpriced", "monthly").getData().getJSONArray("specs").isEmpty());
    }

    @Test public void adminControllerRetainsRegionalFailureSummaryAndGpuAuditModel() throws Exception {
        SystemGpuResourceController controller = new SystemGpuResourceController();
        var field = SystemGpuResourceController.class.getDeclaredField("volcanoCpuSalePriceService");
        field.setAccessible(true); field.set(controller, service);
        client.failing.add("cn-b");
        JSONObject summary = controller.refreshVolcanoCpuCatalog().getData();
        assertEquals(1, summary.getIntValue("succeeded"));
        assertEquals(1, summary.getIntValue("failed"));
        assertEquals("cn-b", summary.getJSONArray("regions").getJSONObject(1).getString("region"));
        assertEquals(1, controller.volcanoCpuCatalog().getData().getJSONArray("regions").size());
        // CPU routes share the same secured controller namespace and audit annotations as GPU routes.
        assertEquals("/system/gpu", SystemGpuResourceController.class.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class).value()[0]);
        for (String method : List.of("volcanoCpuCatalog", "refreshVolcanoCpuCatalog", "volcanoGpuCatalog", "refreshVolcanoGpuCatalog")) {
            assertNotNull(SystemGpuResourceController.class.getMethod(method).getAnnotation(Log.class));
        }
    }

    @Test public void submittedQuoteIsUnaffectedBySubsequentAdminChanges() {
        service.refreshCatalog(); service.saveOrUpdate(edit("monthly", "200"));
        JSONObject quote = service.quote(request("monthly", 2, 3));
        service.saveOrUpdate(edit("monthly", "300"));
        assertEquals(0, new BigDecimal("1200").compareTo(quote.getBigDecimal("total_cost")));
        assertEquals(0, new BigDecimal("1800").compareTo(service.quote(request("monthly", 2, 3)).getBigDecimal("total_cost")));
    }

    private JSONObject displayed(String region) { return service.getDisplayPrices(region, null, "monthly").getJSONArray("specs").getJSONObject(0); }
    private VolcanoCpuSalePriceEdit edit(String billing, String price) {
        VolcanoCpuSalePriceEdit edit = new VolcanoCpuSalePriceEdit(); edit.setRegionCode("cn-a"); edit.setInstanceTypeId("ecs.cpu"); edit.setBillingType(billing); edit.setSalePrice(new BigDecimal(price)); edit.setStatus(1); edit.setRemark("internal-only"); return edit;
    }
    public static JSONObject request(String mode, int duration, int count) {
        JSONObject request = new JSONObject(); request.put("region", "cn-a"); request.put("zone", "cn-a-1"); request.put("instanceTypeId", "ecs.cpu"); request.put("billingMode", mode); request.put("duration", duration); request.put("count", count); return request;
    }
    private static JSONObject catalog(String region, String hourly, String monthly) {
        return JSON.parseObject("{\"success\":true,\"region\":\""+region+"\",\"specs\":[{\"instanceTypeId\":\"ecs.cpu\",\"family\":\"ecs.c\",\"cpuCores\":2,\"memGib\":4,\"price\":{\"available\":true,\"currency\":\"CNY\",\"unitPrice\":"+hourly+"},\"priceMonthly\":{\"available\":true,\"currency\":\"CNY\",\"unitPrice\":"+monthly+"}}]}");
    }
    private static class FakeClient extends InstanceResourceAdapterClient {
        Map<String, JSONObject> catalogs = new HashMap<>(); Set<String> failing = new HashSet<>();
        @Override public JSONObject listCpuRegions() { return JSON.parseObject("{\"success\":true,\"regions\":[{\"region\":\"cn-a\",\"zones\":[\"cn-a-1\"]},{\"region\":\"cn-b\",\"zones\":[\"cn-b-1\"]}]}"); }
        @Override public JSONObject listCpuPrices(String region, boolean refresh) { if (failing.contains(region)) throw new RuntimeException("timeout"); return JSON.parseObject(catalogs.get(region).toJSONString()); }
    }
}
