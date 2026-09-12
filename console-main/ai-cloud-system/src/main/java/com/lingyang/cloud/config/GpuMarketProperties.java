package com.lingyang.cloud.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/** GPU 市场展示配置。地区编码命中 hidden-regions 时不会返回给用户端。 */
@Component
@ConfigurationProperties(prefix = "gpu.market")
public class GpuMarketProperties {

    private List<String> hiddenRegions = new ArrayList<>();

    public List<String> getHiddenRegions() {
        return hiddenRegions;
    }

    public void setHiddenRegions(List<String> hiddenRegions) {
        this.hiddenRegions = hiddenRegions == null ? new ArrayList<>() : hiddenRegions.stream()
                .flatMap(code -> java.util.Arrays.stream(code.split(",")))
                .filter(code -> code != null && !code.isBlank())
                .map(code -> code.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean isHiddenRegion(String regionCode) {
        if (regionCode == null || regionCode.isBlank()) return false;
        String normalized = regionCode.trim().toLowerCase(Locale.ROOT);
        return hiddenRegions.stream()
                .anyMatch(normalized::equals);
    }
}
