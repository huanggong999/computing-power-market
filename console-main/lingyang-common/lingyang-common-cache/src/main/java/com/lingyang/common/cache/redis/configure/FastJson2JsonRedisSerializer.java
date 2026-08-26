package com.lingyang.common.cache.redis.configure;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Redis使用FastJson序列化
 *
 * @author scrm
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    private final Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return JSON.toJSONString(t,
                        JSONWriter.Feature.WriteClassName,
                        JSONWriter.Feature.WriteLongAsString)
                .getBytes(Charset.defaultCharset());
    }
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String str = new String(bytes, StandardCharsets.UTF_8);
        return JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType);
    }

//    @Override
//    public T deserialize(byte[] bytes) throws SerializationException {
//        if (bytes == null || bytes.length == 0) {
//            return null;
//        }
//        String str = new String(bytes, Charset.defaultCharset());
//        int index = str.indexOf(":");
//        String cls = str.substring(2, index - 1);
//        String obj = str.substring(index + 1, str.length() - 1);
//        return JSON.parseObject(
//                obj,
//                clazz,
//                JSONReader.autoTypeFilter(cls));
//    }
}
