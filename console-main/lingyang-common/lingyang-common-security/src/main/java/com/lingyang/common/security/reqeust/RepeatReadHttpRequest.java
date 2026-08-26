package com.lingyang.common.security.reqeust;

import com.lingyang.common.core.utils.StringUtils;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.WebUtils;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/21 14:47
 */
@Slf4j
public class RepeatReadHttpRequest extends HttpServletRequestWrapper {
    private final ByteArrayOutputStream cachedContent;
    private Map<String, String[]> cachedForm;

    public RepeatReadHttpRequest(HttpServletRequest request) {
        super(request);
        this.cachedContent = new ByteArrayOutputStream();
        this.cachedForm = new HashMap<>();
        cacheData();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new RepeatReadInputStream(cachedContent.toByteArray());
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), getCharacterEncoding()));
    }

    @Override
    public String getParameter(String name) {
        String value = null;
        if (isFormPost()) {
            String[] values = cachedForm.get(name);
            if (null != values && values.length > 0) {
                value = values[0];
            }
        }

        if (StringUtils.isEmpty(value)) {
            value = super.getParameter(name);
        }

        return value;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (isFormPost() && ObjectUtils.isNotEmpty(cachedForm)) {
            return cachedForm;
        }

        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (isFormPost() && ObjectUtils.isNotEmpty(cachedForm)) {
            return Collections.enumeration(cachedForm.keySet());
        }
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (isFormPost() && ObjectUtils.isNotEmpty(cachedForm)) {
            return cachedForm.get(name);
        }

        return super.getParameterValues(name);
    }

    private void cacheData() {
        try {
            if (isFormPost()) {
                this.cachedForm = super.getParameterMap();
            } else {
                ServletInputStream inputStream = super.getInputStream();
                IOUtils.copy(inputStream, this.cachedContent);
            }
        } catch (IOException e) {
            log.warn("[RepeatReadHttpRequest:cacheData], error: {}", e.getMessage());
        }

    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null
                && (
                        contentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                        || contentType.contains(MediaType.MULTIPART_FORM_DATA_VALUE)
                )
                && HttpMethod.POST.matches(getMethod()));
    }

    private static class RepeatReadInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public RepeatReadInputStream(byte[] bytes) {
            this.inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read() throws IOException {
            return this.inputStream.read();
        }

        @Override
        public int readLine(byte[] b, int off, int len) throws IOException {
            return this.inputStream.read(b, off, len);
        }

        @Override
        public boolean isFinished() {
            return this.inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
    }
}
