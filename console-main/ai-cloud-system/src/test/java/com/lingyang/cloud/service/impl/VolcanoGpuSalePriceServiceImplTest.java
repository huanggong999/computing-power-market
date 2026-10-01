package com.lingyang.cloud.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.entity.VolcanoGpuSalePriceEntity;
import com.lingyang.cloud.entity.VolcanoGpuCatalogSnapshotEntity;
import com.lingyang.cloud.mapper.VolcanoGpuCatalogSnapshotMapper;
import com.lingyang.cloud.mapper.VolcanoGpuSalePriceMapper;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class VolcanoGpuSalePriceServiceImplTest {

    private VolcanoGpuSalePriceServiceImpl service;
    private Map<String, VolcanoGpuSalePriceEntity> prices;
    private VolcanoGpuCatalogSnapshotEntity snapshot;
    private VolcanoGpuCatalogSnapshotEntity insertedSnapshot;
    private VolcanoGpuCatalogSnapshotEntity updatedSnapshot;

    @Before
    public void setUp() throws Exception {
        service = new VolcanoGpuSalePriceServiceImpl();
        prices = new ConcurrentHashMap<>();
        setField(service, "salePriceMapper", (VolcanoGpuSalePriceMapper) java.lang.reflect.Proxy.newProxyInstance(
                VolcanoGpuSalePriceMapper.class.getClassLoader(),
                new Class<?>[] {VolcanoGpuSalePriceMapper.class},
                (proxy, method, args) -> {
                    if ("selectByUniqueKey".equals(method.getName())) {
                        String key = args[0] + "|" + args[1] + "|" + args[2];
                        return prices.get(key);
                    }
                    if (method.getReturnType().equals(boolean.class)) {
                        return false;
                    }
                    if (method.getReturnType().equals(int.class)) {
                        return 0;
                    }
                    if (method.getReturnType().equals(long.class)) {
                        return 0L;
                    }
                    return null;
                }));
        snapshot = null;
        insertedSnapshot = null;
        updatedSnapshot = null;
        setField(service, "catalogSnapshotMapper", (VolcanoGpuCatalogSnapshotMapper) java.lang.reflect.Proxy.newProxyInstance(
                VolcanoGpuCatalogSnapshotMapper.class.getClassLoader(),
                new Class<?>[] {VolcanoGpuCatalogSnapshotMapper.class},
                (proxy, method, args) -> {
                    if ("selectOne".equals(method.getName())) {
                        return snapshot == null && insertedSnapshot != null ? insertedSnapshot : snapshot;
                    }
                    if ("insert".equals(method.getName())) {
                        insertedSnapshot = (VolcanoGpuCatalogSnapshotEntity) args[0];
                        insertedSnapshot.setId(1L);
                        return 1;
                    }
                    if ("updateById".equals(method.getName())) {
                        updatedSnapshot = (VolcanoGpuCatalogSnapshotEntity) args[0];
                        return 1;
                    }
                    if (method.getReturnType().equals(boolean.class)) {
                        return false;
                    }
                    if (method.getReturnType().equals(int.class)) {
                        return 0;
                    }
                    if (method.getReturnType().equals(long.class)) {
                        return 0L;
                    }
                    return null;
                }));
    }

    @Test
    public void enabledSalePriceOverridesUpstreamAndSupportsOnDemandFallback() {
        prices.put("cn-a|gpu-1|hourly", price("12.5", 1));

        assertEquals(new BigDecimal("12.5"), service.resolveSalePrice(
                "cn-a", "gpu-1", "on_demand", new BigDecimal("5")));
        assertEquals(new BigDecimal("12.5"), service.resolveSalePrice(
                "cn-a", "gpu-1", "hourly", new BigDecimal("5")));
    }

    @Test
    public void disabledSalePriceFallsBackToUpstreamPrice() {
        prices.put("cn-a|gpu-1|hourly", price("12.5", 0));

        assertNull(service.resolvePrice("cn-a", "gpu-1", "hourly"));
        assertEquals(new BigDecimal("5"), service.resolveSalePrice(
                "cn-a", "gpu-1", "hourly", new BigDecimal("5")));
    }

    @Test
    public void missingSalePriceUsesUpstreamPrice() {
        assertEquals(new BigDecimal("5"), service.resolveSalePrice(
                "cn-a", "gpu-1", "hourly", new BigDecimal("5")));
        assertNull(service.resolvePrice("cn-a", "gpu-1", "hourly"));
    }

    @Test
    public void catalogInjectionKeepsUpstreamPriceAndAddsConfiguredSalePrice() {
        prices.put("cn-a|gpu-1|hourly", price("12.5", 1));
        JSONObject instance = new JSONObject();
        instance.put("instanceTypeId", "gpu-1");
        instance.put("price", new BigDecimal("5"));
        instance.put("priceMonthly", new BigDecimal("50"));
        JSONObject catalog = catalog(instance);

        JSONObject result = service.injectSalePrices(catalog);
        JSONObject injected = result.getJSONArray("regions").getJSONObject(0)
                .getJSONArray("gpuSpecs").getJSONObject(0)
                .getJSONArray("instanceTypes").getJSONObject(0);

        assertEquals(0, new BigDecimal("5").compareTo(injected.getBigDecimal("price")));
        assertEquals(0, new BigDecimal("50").compareTo(injected.getBigDecimal("priceMonthly")));
        assertEquals(0, new BigDecimal("12.5").compareTo(injected.getBigDecimal("salePrice")));
        assertEquals(0, new BigDecimal("50").compareTo(injected.getBigDecimal("salePriceMonthly")));
        assertTrue(injected.getBooleanValue("priceConfigured"));
        assertEquals("enabled", injected.getString("priceConfigStatus"));
    }

    @Test
    public void catalogInjectionUsesUpstreamPriceWhenNoEnabledConfigExists() {
        JSONObject instance = new JSONObject();
        instance.put("instanceTypeId", "gpu-1");
        instance.put("price", new BigDecimal("5"));
        instance.put("priceMonthly", new BigDecimal("50"));

        JSONObject result = service.injectSalePrices(catalog(instance));
        JSONObject injected = result.getJSONArray("regions").getJSONObject(0)
                .getJSONArray("gpuSpecs").getJSONObject(0)
                .getJSONArray("instanceTypes").getJSONObject(0);

        assertEquals(0, new BigDecimal("5").compareTo(injected.getBigDecimal("salePrice")));
        assertEquals(0, new BigDecimal("50").compareTo(injected.getBigDecimal("salePriceMonthly")));
        assertFalse(injected.getBooleanValue("priceConfigured"));
        assertEquals("default", injected.getString("priceConfigStatus"));
    }

    @Test
    public void displayCatalogReadsSnapshotTableAndInjectsSalePrices() {
        JSONObject instance = new JSONObject();
        instance.put("instanceTypeId", "gpu-1");
        instance.put("price", new BigDecimal("5"));
        instance.put("priceMonthly", new BigDecimal("50"));
        JSONObject catalog = catalog(instance);
        snapshot = new VolcanoGpuCatalogSnapshotEntity();
        snapshot.setBillingScope("all");
        snapshot.setCatalogJson(catalog.toJSONString());
        prices.put("cn-a|gpu-1|hourly", price("12.5", 1));

        JSONObject result = service.getDisplayCatalog("all");
        JSONObject injected = result.getJSONArray("regions").getJSONObject(0)
                .getJSONArray("gpuSpecs").getJSONObject(0)
                .getJSONArray("instanceTypes").getJSONObject(0);

        assertEquals(0, new BigDecimal("12.5").compareTo(injected.getBigDecimal("salePrice")));
        assertTrue(injected.getBooleanValue("priceConfigured"));
        assertNull(insertedSnapshot);
    }

    @Test
    public void displayCatalogReturnsEmptyForUnsupportedBillingTypes() {
        JSONObject result = service.getDisplayCatalog("daily");

        assertNotNull(result);
        assertNotNull(result.getJSONArray("regions"));
        assertTrue(result.getJSONArray("regions").isEmpty());
        assertNull(insertedSnapshot);
    }

    @Test
    public void snapshotPriceLookupReadsStoredCatalogWithoutCallingScheduler() throws Exception {
        JSONObject instance = new JSONObject();
        instance.put("instanceTypeId", "gpu-1");
        instance.put("price", new BigDecimal("5"));
        instance.put("priceMonthly", new BigDecimal("50"));
        snapshot = new VolcanoGpuCatalogSnapshotEntity();
        snapshot.setBillingScope("all");
        snapshot.setCatalogJson(catalog(instance).toJSONString());

        assertEquals(new BigDecimal("5"), service.findSnapshotGpuPrice(
                "cn-a", null, null, "gpu-1", "on_demand"));
        assertEquals(new BigDecimal("50"), service.findSnapshotGpuPrice(
                "cn-a", null, null, "gpu-1", "monthly"));
    }

    @Test
    public void storedCatalogReadsOnlySnapshotTable() throws Exception {
        GpuSchedulerApiClient client = new GpuSchedulerApiClient() {
            @Override
            public JSONObject getGpuCatalog(String billingType) {
                throw new AssertionError("stored catalog must not call scheduler");
            }
        };
        setField(service, "gpuSchedulerApiClient", client);
        snapshot = new VolcanoGpuCatalogSnapshotEntity();
        snapshot.setBillingScope("all");
        snapshot.setCatalogJson(catalog(new JSONObject()).toJSONString());

        JSONObject result = service.getStoredCatalog("all");

        assertEquals("ok", result.getString("snapshotStatus"));
        assertFalse(result.getJSONArray("regions").isEmpty());
        assertNull(insertedSnapshot);
        assertNull(updatedSnapshot);
    }

    @Test
    public void refreshCatalogFetchesSchedulerAndUpdatesSnapshot() throws Exception {
        JSONObject clientCatalog = catalog(new JSONObject());
        GpuSchedulerApiClient client = new GpuSchedulerApiClient() {
            @Override
            public JSONObject getGpuCatalog(String billingType) {
                return "all".equals(billingType) ? clientCatalog : null;
            }
        };
        setField(service, "gpuSchedulerApiClient", client);

        JSONObject result = service.refreshCatalog("all");

        assertEquals("refreshed", result.getString("snapshotStatus"));
        assertNotNull(insertedSnapshot);
        assertNull(updatedSnapshot);
    }

    @Test
    public void displayCatalogFallsBackToSchedulerAndSavesTableSnapshot() throws Exception {
        JSONObject clientCatalog = catalog(new JSONObject());
        clientCatalog.put("source", "scheduler");
        GpuSchedulerApiClient client = new GpuSchedulerApiClient() {
            @Override
            public JSONObject getGpuCatalog(String billingType) {
                return "all".equals(billingType) ? clientCatalog : null;
            }
        };
        setField(service, "gpuSchedulerApiClient", client);

        JSONObject result = service.getDisplayCatalog("all");

        assertEquals("scheduler", result.getString("source"));
        assertNotNull(insertedSnapshot);
        assertEquals("all", insertedSnapshot.getBillingScope());
        assertEquals("scheduler", insertedSnapshot.getSource());
    }

    private JSONObject catalog(JSONObject instance) {
        JSONObject catalog = new JSONObject();
        JSONArray regions = new JSONArray();
        JSONObject region = new JSONObject();
        region.put("region", "cn-a");
        JSONArray specs = new JSONArray();
        JSONObject spec = new JSONObject();
        spec.put("price", new BigDecimal("5"));
        spec.put("priceMonthly", new BigDecimal("50"));
        spec.put("instanceTypes", new JSONArray(java.util.List.of(instance)));
        specs.add(spec);
        region.put("gpuSpecs", specs);
        regions.add(region);
        catalog.put("regions", regions);
        return catalog;
    }

    private VolcanoGpuSalePriceEntity price(String salePrice, int status) {
        VolcanoGpuSalePriceEntity entity = new VolcanoGpuSalePriceEntity();
        entity.setId(1L);
        entity.setRegionCode("cn-a");
        entity.setInstanceTypeId("gpu-1");
        entity.setBillingType("hourly");
        entity.setSalePrice(new BigDecimal(salePrice));
        entity.setStatus(status);
        return entity;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
