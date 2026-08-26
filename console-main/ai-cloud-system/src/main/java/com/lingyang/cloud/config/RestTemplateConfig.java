package com.lingyang.cloud.config;

import com.alibaba.fastjson2.JSON;
import com.lingyang.common.core.thread.ThreadLocalContext;
import com.lingyang.common.core.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

@Slf4j
@Configuration
public class RestTemplateConfig {

    private static final String MDC_LOG_ID = "logId";

    @Bean
    RestTemplate restTemplate() {
        return create(new SimpleClientHttpRequestFactory());
    }

    public static RestTemplate create(ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(requestFactory));
        restTemplate.getMessageConverters().removeIf(StringHttpMessageConverter.class::isInstance);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.getInterceptors().add(new RestTemplateLogInterceptor());
        return restTemplate;
    }

    private static class RestTemplateLogInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            String requestId = resolveRequestId();
            String previousLogId = MDC.get(MDC_LOG_ID);
            MDC.put(MDC_LOG_ID, requestId);
            try {
                request.getHeaders().set("requestId", requestId);
                ClientHttpResponse response = execution.execute(request, body);
                log.info("requestId: [{}], RestTemplate请求接口: [{} {}], 请求参数: {}, 返回结果: {}",
                        requestId,
                        request.getMethod(),
                        request.getURI(),
                        getRequestParams(body),
                        getResponseResult(response));
                return response;
            } catch (RestClientResponseException e) {
                log.info("requestId: [{}], RestTemplate请求接口: [{} {}], 请求参数: {}, 返回结果: {}",
                        requestId,
                        request.getMethod(),
                        request.getURI(),
                        getRequestParams(body),
                        buildResponseResult(e.getRawStatusCode(), e.getResponseBodyAsString()));
                throw e;
            } finally {
                if (previousLogId == null) {
                    MDC.remove(MDC_LOG_ID);
                } else {
                    MDC.put(MDC_LOG_ID, previousLogId);
                }
            }
        }

        private String resolveRequestId() {
            String requestId = ThreadLocalContext.getRequestId();
            if (StringUtils.isBlank(requestId)) {
                requestId = String.valueOf(IdUtils.nextId());
                ThreadLocalContext.setRequestId(requestId);
            }
            return requestId;
        }

        private String getRequestParams(byte[] body) {
            if (body == null || body.length == 0) {
                return "";
            }
            return normalizeJsonText(new String(body, StandardCharsets.UTF_8));
        }

        private String getResponseResult(ClientHttpResponse response) throws IOException {
            byte[] body = StreamUtils.copyToByteArray(response.getBody());
            if (body.length == 0) {
                return buildResponseResult(response.getStatusCode().value(), null);
            }
            Charset charset = StandardCharsets.UTF_8;
            if (response.getHeaders().getContentType() != null && response.getHeaders().getContentType().getCharset() != null) {
                charset = response.getHeaders().getContentType().getCharset();
            }
            return buildResponseResult(response.getStatusCode().value(), new String(body, charset));
        }

        private String buildResponseResult(int status, String body) {
            com.alibaba.fastjson2.JSONObject responseResult = new com.alibaba.fastjson2.JSONObject();
            responseResult.put("status", status);
            if (StringUtils.isNotBlank(body)) {
                responseResult.put("body", parseJsonIfPossible(body));
            }
            return responseResult.toJSONString();
        }

        private Object parseJsonIfPossible(String text) {
            if (StringUtils.isBlank(text)) {
                return text;
            }
            try {
                return JSON.parse(text);
            } catch (Exception ignored) {
                return text;
            }
        }

        private String normalizeJsonText(String text) {
            Object parsed = parseJsonIfPossible(text);
            return parsed instanceof String ? (String) parsed : JSON.toJSONString(parsed);
        }
    }
}
