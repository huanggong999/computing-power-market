package com.lingyang.common.web.serializer.handler;

import com.lingyang.common.web.serializer.PrivacySerializerInterface;
import com.lingyang.common.web.serializer.constant.ReplaceConstant;
import reactor.util.annotation.NonNull;

import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 16:54
 */
public class PhonePrivacySerializer implements PrivacySerializerInterface {
    @Override
    public String serializer(@NonNull String phone) throws IOException {
        return phone.replaceAll(ReplaceConstant.PHONE_REPLACE, "$1*****$2");
    }
}