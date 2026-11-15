package com.lingyang.cloud.utils;

import com.lingyang.cloud.entity.SysCustomerVolumeEntity;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomerVolumeMapUtilsTest {

    @Test
    public void duplicateVolumeWithSameOwnershipUsesLowestId() {
        SysCustomerVolumeEntity largerId = volume(
                252L, 100L, 200L, "order-source", "vol-shared", "i-second");
        SysCustomerVolumeEntity smallerId = volume(
                250L, 100L, 200L, "order-source", "vol-shared", "i-first");

        Map<String, SysCustomerVolumeEntity> result = CustomerVolumeMapUtils.indexByVolumeId(
                List.of(largerId, smallerId));

        assertTrue(result.containsKey("vol-shared"));
        assertEquals(Long.valueOf(250L), result.get("vol-shared").getId());
        assertEquals("i-first", result.get("vol-shared").getInstanceId());
    }

    @Test
    public void duplicateVolumeWithDifferentOwnersIsExcludedFromSettlement() {
        SysCustomerVolumeEntity firstOwner = volume(
                250L, 100L, 200L, "order-a", "vol-conflict", "i-first");
        SysCustomerVolumeEntity secondOwner = volume(
                252L, 101L, 201L, "order-b", "vol-conflict", "i-second");

        Map<String, SysCustomerVolumeEntity> result = CustomerVolumeMapUtils.indexByVolumeId(
                List.of(firstOwner, secondOwner));

        assertFalse(result.containsKey("vol-conflict"));
    }

    private SysCustomerVolumeEntity volume(
            Long id,
            Long customerId,
            Long orderId,
            String orderSourceUid,
            String volumeId,
            String instanceId) {
        SysCustomerVolumeEntity volume = new SysCustomerVolumeEntity();
        volume.setId(id);
        volume.setCustomerId(customerId);
        volume.setOrderId(orderId);
        volume.setOrderSourceUid(orderSourceUid);
        volume.setVolumeId(volumeId);
        volume.setInstanceId(instanceId);
        return volume;
    }
}
