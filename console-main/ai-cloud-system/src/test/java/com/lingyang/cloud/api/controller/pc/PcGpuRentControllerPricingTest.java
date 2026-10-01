package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.client.GpuSchedulerApiClient;
import com.lingyang.cloud.model.dto.GpuRentCalculateDTO;
import com.lingyang.cloud.model.dto.GpuRentOrderDTO;
import com.lingyang.cloud.model.vo.pc.GpuRentFeeVO;
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import com.lingyang.cloud.service.VolcanoGpuSalePriceService;
import com.lingyang.common.core.model.result.Result;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PcGpuRentControllerPricingTest {

    private PcGpuRentController controller;
    private int schedulerCalls;

    @Before
    public void setUp() throws Exception {
        controller = new PcGpuRentController();
        schedulerCalls = 0;
        setField(controller, "gpuSchedulerApiClient", new GpuSchedulerApiClient() {
            public BigDecimal findGpuPrice(String region, String model, String memory,
                    String instanceTypeId, String billingType) {
                schedulerCalls++;
                return "fallback".equals(instanceTypeId) ? new BigDecimal("6") : null;
            }
        });
        setField(controller, "volcanoGpuSalePriceService", new VolcanoGpuSalePriceService() {
            public com.lingyang.common.core.model.result.PageResult<com.lingyang.cloud.model.vo.system.VolcanoGpuSalePriceVO> getPricePage(
                    com.lingyang.common.core.model.page.PageQuery pageQuery, String regionCode, String instanceTypeId,
                    String gpuModel, String billingType, Integer status) {
                return null;
            }

            public com.lingyang.cloud.model.vo.system.VolcanoGpuSalePriceVO getById(Long id) {
                return null;
            }

            public void saveOrUpdate(com.lingyang.cloud.model.edit.gpu.VolcanoGpuSalePriceEdit edit) {
            }

            public void updateStatus(Long id, Integer status) {
            }

            public void restoreDefaultPrice(Long id) {
            }

            public com.lingyang.cloud.entity.VolcanoGpuSalePriceEntity resolvePrice(
                    String regionCode, String instanceTypeId, String billingType) {
                return null;
            }

            public BigDecimal resolveSalePrice(String region, String instanceTypeId,
                    String billingType, BigDecimal upstreamPrice) {
                return "no-sale".equals(instanceTypeId) ? BigDecimal.ZERO : new BigDecimal("8");
            }

            public com.alibaba.fastjson2.JSONObject injectSalePrices(com.alibaba.fastjson2.JSONObject catalog) {
                return null;
            }

            public com.alibaba.fastjson2.JSONObject getDisplayCatalog(String billingType) {
                return null;
            }

            public com.alibaba.fastjson2.JSONObject getStoredCatalog(String billingType) {
                return null;
            }

            public BigDecimal findSnapshotGpuPrice(String region, String model, String memory,
                    String instanceTypeId, String billingType) {
                return "missing".equals(instanceTypeId) || "fallback".equals(instanceTypeId)
                        ? null : new BigDecimal("5");
            }

            public com.alibaba.fastjson2.JSONObject refreshCatalog(String billingType) {
                return null;
            }
        });
    }

    @Test
    public void externalFeeUsesSnapshotPriceAndDoesNotCallScheduler() throws Exception {
        GpuRentFeeVO fee = calculateExternal("gpu-1");

        assertNotNull(fee);
        assertEquals(0, new BigDecimal("5").compareTo(fee.getUpstreamUnitPrice()));
        assertEquals(0, new BigDecimal("8").compareTo(fee.getSaleUnitPrice()));
        assertEquals(0, new BigDecimal("8").compareTo(fee.getUnitPrice()));
        assertEquals(0, new BigDecimal("3").compareTo(fee.getPremiumUnitAmount()));
        assertEquals(0, new BigDecimal("16").compareTo(fee.getTotal()));
        assertEquals(0, schedulerCalls);
    }

    @Test
    public void externalFeeDoesNotFallbackToSchedulerWhenSnapshotPriceIsMissing() throws Exception {
        Result<?> result = calculate("missing");

        assertNotNull(result);
        assertNull(result.getData());
        assertEquals(0, schedulerCalls);
    }

    @Test
    public void externalOrderFallsBackToSchedulerOnlyWhenSnapshotPriceIsMissing() throws Exception {
        Result<?> result = calculateForOrder("fallback");
        GpuRentFeeVO fee = (GpuRentFeeVO) result.getData();

        assertNotNull(fee);
        assertEquals(0, new BigDecimal("6").compareTo(fee.getUpstreamUnitPrice()));
        assertEquals(1, schedulerCalls);
    }

    @Test
    public void externalOrderIsRejectedWhenConfiguredSalePriceIsInvalid() throws Exception {
        Result<?> result = calculate("no-sale");

        assertNotNull(result);
        assertNull(result.getData());
    }

    private Result<?> calculateForOrder(String instanceTypeId) throws Exception {
        GpuRentOrderDTO dto = new GpuRentOrderDTO();
        dto.setResourceId(0L);
        dto.setBillingType("on_demand");
        dto.setQuantity(2);
        dto.setDuration(1);
        dto.setPodCreateRequest(request(instanceTypeId));
        Method method = PcGpuRentController.class.getDeclaredMethod(
                "calculateExternalForOrder", GpuRentCalculateDTO.class, GpuPodCreateRequest.class);
        method.setAccessible(true);
        return (Result<?>) method.invoke(controller, dto, dto.getPodCreateRequest());
    }

    private GpuRentFeeVO calculateExternal(String instanceTypeId) throws Exception {
        Result<?> result = calculate(instanceTypeId);
        return (GpuRentFeeVO) result.getData();
    }

    private Result<?> calculate(String instanceTypeId) throws Exception {
        GpuRentOrderDTO dto = new GpuRentOrderDTO();
        dto.setResourceId(0L);
        dto.setBillingType("on_demand");
        dto.setQuantity(2);
        dto.setDuration(1);
        dto.setPodCreateRequest(request(instanceTypeId));
        Method method = PcGpuRentController.class.getDeclaredMethod(
                "calculateExternal", GpuRentCalculateDTO.class, GpuPodCreateRequest.class);
        method.setAccessible(true);
        return (Result<?>) method.invoke(controller, dto, dto.getPodCreateRequest());
    }

    private GpuPodCreateRequest request(String instanceTypeId) {
        GpuPodCreateRequest request = new GpuPodCreateRequest();
        GpuPodCreateRequest.GpuSpec spec = new GpuPodCreateRequest.GpuSpec();
        spec.setModel("H20");
        spec.setGpuMemory("96GB");
        request.setGpuSpec(spec);
        request.setRegion("cn-a");
        request.setMachineId(instanceTypeId);
        return request;
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
