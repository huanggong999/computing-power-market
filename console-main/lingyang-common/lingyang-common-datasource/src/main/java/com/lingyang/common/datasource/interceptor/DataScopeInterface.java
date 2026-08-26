package com.lingyang.common.datasource.interceptor;

import com.lingyang.common.core.datasource.annotation.DataScope;
import com.lingyang.common.core.datasource.enums.DataScopeType;
import org.apache.commons.lang3.ObjectUtils;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/10 18:09
 */
public interface DataScopeInterface {

    String CURSOR_NAME = "dataScopeDb";

    String SQL = "select * from (%s) " + CURSOR_NAME + " where %s";

    /**
     * 重置sql
     *
     * @param oldSql 原sql
     * @return 具有数据权限的sql
     */
    default String resetSql(String oldSql, DataScope dataScope) {
        List<Long> userIdList = userId();
        String where = "1=2";
        if (ObjectUtils.isNotEmpty(userIdList)) {
            StringBuilder inSb = new StringBuilder();
            for (Long userId : userIdList) {
                inSb.append(userId).append(",");
            }
            where = CURSOR_NAME + "." + dataScope.userIdFiled() + " in (" + inSb.substring(inSb.length() - 1) + ")";
        }
        return String.format(SQL, oldSql, where);
    }

    /**
     * 处理用户id
     *
     * @return 用户id
     */
    List<Long> userId();

    /**
     * 获取拦截类型
     *
     * @return DataScopeType
     */
    DataScopeType dataSourceType();
}
