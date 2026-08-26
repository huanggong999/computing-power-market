package com.lingyang.cloud.tencent.pay;

import com.lingyang.common.core.exception.MethodExecutionException;
import com.lingyang.common.core.utils.StringUtils;
import com.wechat.pay.java.core.AbstractRSAConfig;
import com.wechat.pay.java.core.AbstractRSAConfigBuilder;
import com.wechat.pay.java.core.certificate.CertificateProvider;
import com.wechat.pay.java.core.certificate.RSAAutoCertificateProvider;
import com.wechat.pay.java.core.cipher.AeadAesCipher;
import com.wechat.pay.java.core.cipher.AeadCipher;
import com.wechat.pay.java.core.cipher.RSAVerifier;
import com.wechat.pay.java.core.cipher.Verifier;
import com.wechat.pay.java.core.http.AbstractHttpClientBuilder;
import com.wechat.pay.java.core.http.HttpClient;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.util.IOUtil;
import com.wechat.pay.java.core.util.PemUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/24 18:29
 */
public class CustomRSAAutoCertificateConfig extends AbstractRSAConfig implements NotificationConfig {
    private final CertificateProvider certificateProvider;
    private final AeadCipher aeadCipher;

    private CustomRSAAutoCertificateConfig(Builder builder) {
        super(builder.getMerchantId(), builder.getPrivateKey(), builder.getMerchantSerialNumber(), builder.certificateProvider);
        this.certificateProvider = builder.certificateProvider;
        this.aeadCipher = new AeadAesCipher(builder.apiV3Key);
    }

    @Override
    public String getSignType() {
        return "WECHATPAY2-SHA256-RSA2048";
    }

    @Override
    public String getCipherType() {
        return "AEAD_AES_256_GCM";
    }

    @Override
    public Verifier createVerifier() {
        return new RSAVerifier(this.certificateProvider);
    }

    @Override
    public AeadCipher createAeadCipher() {
        return this.aeadCipher;
    }

    public static class Builder extends AbstractRSAConfigBuilder<Builder> {
        protected HttpClient httpClient;
        protected byte[] apiV3Key;
        protected CertificateProvider certificateProvider;
        protected AbstractHttpClientBuilder<?> httpClientBuilder;

        public Builder() {
        }

        public Builder apiV3Key(String apiV3key) {
            this.apiV3Key = apiV3key.getBytes(StandardCharsets.UTF_8);
            return this.self();
        }

        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder httpClientBuilder(AbstractHttpClientBuilder<?> builder) {
            this.httpClientBuilder = builder;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Builder privateKeyFromPath(String keyPath) {
            if (StringUtils.isEmpty(keyPath)) {
                throw new MethodExecutionException("keyPath is null");
            }
            if (keyPath.startsWith("classPath:")) {
                try {
                    InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(keyPath.substring("classPath:".length()));
                    assert resourceAsStream != null;
                    String string = IOUtil.toString(resourceAsStream);
                    this.privateKey = PemUtil.loadPrivateKeyFromString(string);
                } catch (Exception e) {
                    throw new MethodExecutionException(e);
                }
            } else {
                this.privateKey = PemUtil.loadPrivateKeyFromPath(keyPath);
            }
            return this.self();
        }

        protected String getMerchantId() {
            return merchantId;
        }

        protected PrivateKey getPrivateKey() {
            return privateKey;
        }

        protected String getMerchantSerialNumber() {
            return merchantSerialNumber;
        }

        public CustomRSAAutoCertificateConfig build() {
            RSAAutoCertificateProvider.Builder providerBuilder = (new RSAAutoCertificateProvider.Builder())
                    .merchantId(Objects.requireNonNull(this.merchantId))
                    .apiV3Key(Objects.requireNonNull(this.apiV3Key))
                    .privateKey(Objects.requireNonNull(this.privateKey))
                    .merchantSerialNumber(Objects.requireNonNull(this.merchantSerialNumber));
            if (this.httpClient != null) {
                providerBuilder.httpClient(this.httpClient);
            }

            if (this.httpClientBuilder != null) {
                providerBuilder.httpClientBuilder(this.httpClientBuilder);
            }

            this.certificateProvider = providerBuilder.build();
            return new CustomRSAAutoCertificateConfig(this);
        }
    }
}
