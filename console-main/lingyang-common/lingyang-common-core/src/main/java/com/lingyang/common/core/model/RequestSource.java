package com.lingyang.common.core.model;

import com.lingyang.common.core.utils.StringUtils;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/21 10:10
 */
@Data
public class RequestSource {
    public static Map<String, RequestSource> SOURCE_MAP;
    private String sourceKey;
    private RequestSource(String sourceKey) {
        this.sourceKey = sourceKey;
    }
    public static RequestSource create(String sourceKey) {
        if (SOURCE_MAP == null ) {
            SOURCE_MAP = new ConcurrentHashMap<>();
        }
        RequestSource requestSource = new RequestSource(sourceKey);
        SOURCE_MAP.put(sourceKey, requestSource);
        return requestSource;
    }

    public static RequestSource getSource(String sourceKey) {
        if (StringUtils.isEmpty(sourceKey)) {
            return null;
        }
        return SOURCE_MAP.get(sourceKey);
    }
}