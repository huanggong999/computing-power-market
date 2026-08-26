package com.lingyang.common.datasource.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/25 17:54
 */
@Configuration
@ConfigurationProperties(prefix = "page-helper")
@Data
public class PageHelperConfig {
    /**
     * 是否支持接口参数来传递分页参数,默认false
     */
    private boolean supportMethodsArguments = false;

    /**
     * RowBounds参数offset作为PageNum使用 - 默认不使用
     */
    private boolean offsetAsPageNum = false;

    /**
     * RowBounds是否进行count查询 - 默认不查询
     */
    private boolean rowBoundsWithCount = false;

    /**
     * 当设置为true的时候，如果pageSize设置为0（或RowBounds的limit=0），就不执行分页，返回全部结果
     */
    private boolean pageSizeZero = false;

    /**
     * 分页合理化，默认false
     */
    private boolean reasonable = false;

    /**
     * 默认count(0)
     */
    private String countColumn = "0";

    /**
     * 数据源类型
     */
    private String helperDialect = "mysql";

    /**
     * 数据库方言配置类
     */
    private String dialectClass = "com.lingyang.common.datasource.page.PageDialect";
}