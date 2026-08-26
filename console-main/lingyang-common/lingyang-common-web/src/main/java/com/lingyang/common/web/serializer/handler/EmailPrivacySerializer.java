package com.lingyang.common.web.serializer.handler;

import com.lingyang.common.web.serializer.PrivacySerializerInterface;
import com.lingyang.common.web.serializer.constant.ReplaceConstant;
import reactor.util.annotation.NonNull;

import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 16:53
 */
public class EmailPrivacySerializer implements PrivacySerializerInterface {
    @Override
    public String serializer(@NonNull String email) throws IOException {
        return email.replaceAll(ReplaceConstant.EMAIL_REPLACE, "$1****$3$4");
    }
}
