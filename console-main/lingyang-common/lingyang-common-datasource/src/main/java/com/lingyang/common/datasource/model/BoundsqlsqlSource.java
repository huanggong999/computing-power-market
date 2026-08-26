package com.lingyang.common.datasource.model;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;

/**
 * @Deprecated
 * @Author 王小龙
 * @Time 2023/3/3 15:00
 */
public class BoundsqlsqlSource implements SqlSource {
    private final BoundSql boundsql;

    public BoundsqlsqlSource(BoundSql boundsql) {
        this.boundsql = boundsql;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return boundsql;
    }
}
