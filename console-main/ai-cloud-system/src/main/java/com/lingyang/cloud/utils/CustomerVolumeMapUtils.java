package com.lingyang.cloud.utils;

import com.lingyang.cloud.entity.SysCustomerVolumeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 客户云盘资源索引工具。
 */
@Slf4j
public final class CustomerVolumeMapUtils {

    private CustomerVolumeMapUtils() {
    }

    /**
     * 按火山云盘 ID 建立结算索引。
     *
     * <p>历史数据可能把同一云盘复制给多个实例。对于归属一致的重复记录，
     * 固定使用主键最小的一条，避免整批账单因 {@code Collectors.toMap}
     * 的重复 key 异常而中断；如果客户或订单不一致，则跳过该云盘账单，
     * 防止把费用结算到错误客户。</p>
     */
    public static Map<String, SysCustomerVolumeEntity> indexByVolumeId(
            Collection<SysCustomerVolumeEntity> volumes) {
        if (volumes == null || volumes.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, List<SysCustomerVolumeEntity>> groupedVolumes = volumes.stream()
                .filter(Objects::nonNull)
                .filter(volume -> StringUtils.isNotEmpty(volume.getVolumeId()))
                .collect(Collectors.groupingBy(
                        SysCustomerVolumeEntity::getVolumeId,
                        LinkedHashMap::new,
                        Collectors.toList()));

        Map<String, SysCustomerVolumeEntity> result = new HashMap<>(groupedVolumes.size());
        groupedVolumes.forEach((volumeId, candidates) -> {
            SysCustomerVolumeEntity selected = candidates.stream()
                    .min(Comparator.comparing(
                            SysCustomerVolumeEntity::getId,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .orElseThrow();
            if (candidates.size() == 1) {
                result.put(volumeId, selected);
                return;
            }

            boolean ownershipConsistent = candidates.stream().allMatch(candidate ->
                    Objects.equals(selected.getCustomerId(), candidate.getCustomerId())
                            && Objects.equals(selected.getOrderId(), candidate.getOrderId())
                            && Objects.equals(selected.getOrderSourceUid(), candidate.getOrderSourceUid()));
            if (!ownershipConsistent) {
                log.error("检测到重复云盘且归属不一致，已跳过该云盘账单结算，volumeId={}, records={}",
                        volumeId, describe(candidates));
                return;
            }

            log.warn("检测到重复云盘记录，结算使用主键最小的记录，volumeId={}, retainedId={}, records={}",
                    volumeId, selected.getId(), describe(candidates));
            result.put(volumeId, selected);
        });
        return result;
    }

    static String describe(List<SysCustomerVolumeEntity> candidates) {
        return candidates.stream()
                .map(volume -> String.format(
                        "{id=%s, customerId=%s, orderId=%s, instanceId=%s}",
                        volume.getId(), volume.getCustomerId(), volume.getOrderId(), volume.getInstanceId()))
                .collect(Collectors.joining(", "));
    }
}
