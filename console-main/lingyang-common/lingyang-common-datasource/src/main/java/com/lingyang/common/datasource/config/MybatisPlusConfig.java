package com.lingyang.common.datasource.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.security.model.LoginUserInfoDetail;
import com.lingyang.common.core.utils.DateUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationServiceException;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/3/1 17:40
 */
@Configuration
public class MybatisPlusConfig implements MetaObjectHandler {

    private static final String CREATE_TIME = "createTime";
    private static final String CREATE_BY = "createBy";
    private static final String CREATE_BY_ID = "createById";
    private static final String UPDATE_TIME = "updateTime";
    private static final String UPDATE_BY = "updateBy";
    private static final String UPDATE_BY_ID = "updateById";

    @Override
    public void insertFill(MetaObject metaObject) {
        //创建人时间
        setValue(CREATE_TIME, DateUtils.getNowDate(), metaObject);
        try {
            LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
            setValue(CREATE_BY, userInfo.getUsername(), metaObject);
            setValue(CREATE_BY_ID, userInfo.getUserId(), metaObject);
        }catch (AuthenticationServiceException ignored) {
        }

        this.updateFill(metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        //更新人时间
        setValue(UPDATE_TIME, DateUtils.getNowDate(), metaObject);
        try {
            LoginUserInfoDetail userInfo = SecurityContext.getUserInfo();
            setValue(UPDATE_BY, userInfo.getUsername(), metaObject);
            setValue(UPDATE_BY_ID, userInfo.getUserId(), metaObject);
        }catch (AuthenticationServiceException ignored) {
        }
    }

    private void setValue(String filedName, Object value, MetaObject metaObject) {
        if (ObjectUtils.isEmpty(this.getFieldValByName(filedName, metaObject))) {
            this.setFieldValByName(filedName, value, metaObject);
        }
    }


}