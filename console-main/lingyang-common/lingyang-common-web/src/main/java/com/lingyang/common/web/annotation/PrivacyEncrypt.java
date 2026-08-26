package com.lingyang.common.web.annotation;

/**
 * @Description: 数据脱敏注解
 * @Author: 王小龙
 * @Date: 2024/4/15 14:06
 */

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lingyang.common.web.serializer.PrivacySerializer;
import com.lingyang.common.web.serializer.PrivacySerializerInterface;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = PrivacySerializer.class)
public @interface PrivacyEncrypt {

    /**
     * 脱敏序列化接口
     * @return 脱敏序列化接口Class
     */
    Class<? extends PrivacySerializerInterface> serializer();
}
