package com.lingyang.common.web.serializer.handler;

import com.lingyang.common.web.serializer.PrivacySerializerInterface;
import com.lingyang.common.web.serializer.constant.ReplaceConstant;
import reactor.util.annotation.NonNull;

import java.io.IOException;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/4/15 16:46
 */
public class IdCardPrivacySerializer implements PrivacySerializerInterface {

    @Override
    public String serializer(@NonNull String origin) throws IOException {
        return origin.replaceAll(ReplaceConstant.ID_CARD_REPLACE, "$1*****$2");
    }
}
