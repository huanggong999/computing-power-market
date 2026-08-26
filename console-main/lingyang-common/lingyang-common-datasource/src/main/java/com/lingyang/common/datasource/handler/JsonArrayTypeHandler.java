package com.lingyang.common.datasource.handler;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.lingyang.common.core.utils.StringUtils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/10/12 16:57
 */

public class JsonArrayTypeHandler extends AbstractJsonTypeHandler<JSONArray> {


    @Override
    protected JSONArray parse(String json) {
        if (StringUtils.isNotEmpty(json)) {
            return JSONArray.parseArray(json);
        }
        return null;
    }

    @Override
    protected String toJson(JSONArray obj) {
        if (obj != null ) {
            return obj.toJSONString();
        }
        return null;
    }
}
