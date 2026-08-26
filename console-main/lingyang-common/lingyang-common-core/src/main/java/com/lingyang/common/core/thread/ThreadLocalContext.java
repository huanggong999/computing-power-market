package com.lingyang.common.core.thread;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.lingyang.common.core.utils.IdUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/8 16:01
 */
public class ThreadLocalContext {
    private static final TransmittableThreadLocal<Map<String, Object>> THREAD_LOCAL = new TransmittableThreadLocal<>();

    private static final String LOG_REQUEST_ID_KEY = "log_request_id";
    private static final String REQUEST_ID_KEY = "request_id";
    public static  void set(String key, Object data) {
        getLocalMap().put(key, data);
    }

    @SuppressWarnings(value = {"rawtypes", "unchecked"})
    public static <T> T get(String key) {
        Object data = getLocalMap().get(key);
        return data == null ? null : (T) data;
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }

    public static void setRequestLogId(Long id) {
        getLocalMap().put(LOG_REQUEST_ID_KEY, id);
    }

    public static void setRequestId(String requestId) {
        getLocalMap().put(REQUEST_ID_KEY, requestId);
    }

    public static String getRequestId() {
        Object requestId = getLocalMap().get(REQUEST_ID_KEY);
        return requestId == null ? null : requestId.toString();
    }

    public static Long getRequestLogId() {
        Object requestLogId = getLocalMap().get(LOG_REQUEST_ID_KEY);
        return requestLogId == null ? IdUtils.nextId() : (Long) requestLogId;
    }

    public static Map<String, Object> getLocalMap() {
        Map<String, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new ConcurrentHashMap<>();
            THREAD_LOCAL.set(map);
        }
        return map;
    }

}
