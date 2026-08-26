package com.lingyang.common.datasource.handler;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.lingyang.common.core.utils.StringUtils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/10/19 18:33
 */
public class JsonObjectTypeHandler extends AbstractJsonTypeHandler<JSONObject> {


    @Override
    protected JSONObject parse(String json) {
        if (StringUtils.isNotEmpty(json)) {
            return JSONObject.parseObject(json);
        }
        return null;
    }

    @Override
    protected String toJson(JSONObject obj) {
        if (obj != null ) {
            return obj.toJSONString();
        }
        return null;
    }
}
