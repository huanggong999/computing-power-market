package com.lingyang.common.http.interceptor;

import com.dtflys.forest.backend.ContentType;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import com.dtflys.forest.interceptor.Interceptor;
import com.lingyang.common.http.exception.RpcClientException;
import com.lingyang.common.http.model.HttpClientResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/8/20 15:05
 */
@Component
@Slf4j
public class HttpInterceptor<T extends HttpClientResult> implements Interceptor<T> {

    @Override
    public void afterExecute(ForestRequest request, ForestResponse response) {
        ContentType contentType = response.getContentType();
        if (ObjectUtils.isNotEmpty(contentType) && contentType.isJson() && response.getResult() instanceof HttpClientResult baseClientResult) {
            if (!baseClientResult.success()) {
                throw new RpcClientException(baseClientResult.getMsg());
            }
        }
    }
}
