package com.lingyang.common.web.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.lingyang.common.core.utils.ClassUtils;
import com.lingyang.common.core.utils.Optional;
import com.lingyang.common.core.utils.SpringUtils;
import com.lingyang.common.core.utils.StringUtils;
import com.lingyang.common.web.annotation.PrivacyEncrypt;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 14:07
 */
@AllArgsConstructor
@NoArgsConstructor
public class PrivacySerializer extends JsonSerializer<String> implements ContextualSerializer {
    /**
     * 序列化接口
     */
    private PrivacySerializerInterface privacySerializerInterface;

    @Override
    public void serialize(String origin, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (StringUtils.isNotEmpty(origin) && null != privacySerializerInterface) {
            jsonGenerator.writeString(privacySerializerInterface.serializer(origin));
        } else {
            jsonGenerator.writeNull();
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return Optional.of(beanProperty)
                .filterMap(property -> Objects.equals(property.getType().getRawClass(), String.class),
                        property -> {
                            PrivacyEncrypt annotation = property.getAnnotation(PrivacyEncrypt.class);
                            return annotation == null ? beanProperty.getContextAnnotation(PrivacyEncrypt.class) : annotation;
                        })
                .flatMap((Function<PrivacyEncrypt, JsonSerializer<?>>) encrypt -> {
                    Class<? extends PrivacySerializerInterface> serializerClass = encrypt.serializer();
                    PrivacySerializerInterface serializer;
                    try {
                        serializer = SpringUtils.getBean(serializerClass);
                    } catch (NoSuchBeanDefinitionException noSuchBeanDefinitionException) {
                        serializer = ClassUtils.newInstance(serializerClass);
                    }
                    this.privacySerializerInterface = serializer;
                    return this;
                })
                .orElseGet(() -> {
                    try {
                        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
                    } catch (JsonMappingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
