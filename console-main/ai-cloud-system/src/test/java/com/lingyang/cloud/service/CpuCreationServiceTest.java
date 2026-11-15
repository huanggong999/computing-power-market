package com.lingyang.cloud.service;

import com.alibaba.fastjson2.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CpuCreationServiceTest {
    @Test
    public void validatesCpuPurchaseRangesAndAllowsZeroDataDisk() {
        JSONObject request = new JSONObject();
        request.put("systemDiskGb", 20);
        request.put("dataDiskGb", 0);
        request.put("count", 10);
        CpuCreationService.integer(request, "systemDiskGb", 20, 2048);
        CpuCreationService.integer(request, "dataDiskGb", 0, 8192);
        CpuCreationService.integer(request, "count", 1, 10);
        request.put("count", 11);
        try {
            CpuCreationService.integer(request, "count", 1, 10);
            fail("out of range count must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("1–10"));
        }
    }

    @Test
    public void operationResponseIsAcceptedButFailedBusinessResultIsNotSuccess() {
        Map<String, Object> row = new HashMap<>();
        row.put("operation_id", "op-1");
        row.put("status", "failed");
        row.put("message", "资源不足");
        JSONObject result = CpuCreationService.response(row);
        assertEquals("op-1", result.getString("operation_id"));
        assertFalse(result.getBooleanValue("success"));
        assertTrue(result.getBooleanValue("accepted"));
    }
}
