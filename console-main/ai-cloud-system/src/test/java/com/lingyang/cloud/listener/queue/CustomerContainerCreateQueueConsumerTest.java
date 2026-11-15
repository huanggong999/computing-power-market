package com.lingyang.cloud.listener.queue;

import com.lingyang.cloud.entity.SysCustomerVolumeEntity;
import com.volcengine.storageebs.model.VolumeForDescribeVolumesOutput;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class CustomerContainerCreateQueueConsumerTest {

    @Test
    public void assignsEachInstanceItsOwnSystemAndDataVolumeIds() {
        List<SysCustomerVolumeEntity> volumes = List.of(
                customerVolume("system"),
                customerVolume("data"));
        Map<String, List<VolumeForDescribeVolumesOutput>> providerVolumes = Map.of(
                "system", List.of(providerVolume("system", "vol-system-1")),
                "data", List.of(providerVolume("data", "vol-data-1")));

        CustomerContainerCreateQueueConsumer.assignVolumeIds(
                volumes, providerVolumes, "i-instance-1", 200L, 100L);

        assertEquals("vol-system-1", volumes.get(0).getVolumeId());
        assertEquals("vol-data-1", volumes.get(1).getVolumeId());
        assertEquals("i-instance-1", volumes.get(0).getInstanceId());
        assertEquals(Long.valueOf(200L), volumes.get(0).getCustomerInstanceId());
        assertEquals(Long.valueOf(100L), volumes.get(0).getCustomerId());
    }

    @Test
    public void secondInstanceDoesNotReuseFirstInstanceVolumeId() {
        List<SysCustomerVolumeEntity> secondInstanceVolumes = List.of(
                customerVolume("system"),
                customerVolume("data"));
        Map<String, List<VolumeForDescribeVolumesOutput>> secondProviderVolumes = Map.of(
                "system", List.of(providerVolume("system", "vol-system-2")),
                "data", List.of(providerVolume("data", "vol-data-2")));

        CustomerContainerCreateQueueConsumer.assignVolumeIds(
                secondInstanceVolumes, secondProviderVolumes, "i-instance-2", 201L, 100L);

        assertEquals("vol-system-2", secondInstanceVolumes.get(0).getVolumeId());
        assertEquals("vol-data-2", secondInstanceVolumes.get(1).getVolumeId());
        assertEquals("i-instance-2", secondInstanceVolumes.get(1).getInstanceId());
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsMissingProviderVolumeInsteadOfContinuingCreation() {
        List<SysCustomerVolumeEntity> volumes = List.of(
                customerVolume("system"),
                customerVolume("data"));
        Map<String, List<VolumeForDescribeVolumesOutput>> providerVolumes = Map.of(
                "system", List.of(providerVolume("system", "vol-system-1")));

        CustomerContainerCreateQueueConsumer.assignVolumeIds(
                volumes, providerVolumes, "i-instance-1", 200L, 100L);
    }

    private SysCustomerVolumeEntity customerVolume(String kind) {
        SysCustomerVolumeEntity volume = new SysCustomerVolumeEntity();
        volume.setKind(kind);
        return volume;
    }

    private VolumeForDescribeVolumesOutput providerVolume(String kind, String volumeId) {
        VolumeForDescribeVolumesOutput volume = new VolumeForDescribeVolumesOutput();
        volume.setKind(kind);
        volume.setVolumeId(volumeId);
        return volume;
    }
}
