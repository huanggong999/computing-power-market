package com.lingyang.common.web.serializer;

import reactor.util.annotation.NonNull;

import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 14:14
 */
public interface PrivacySerializerInterface {

    /**
     * 序列化接口
     * @param origin 源数据字符串
     * @return 脱敏后的字符串
     * @throws IOException 序列化异常
     */
    String serializer(@NonNull String origin) throws IOException;
}
