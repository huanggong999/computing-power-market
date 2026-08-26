package com.lingyang.common.core.datasource.annotation;


import java.lang.annotation.*;

/**
 * @Description: 数据权限过滤注解
 * @Author: 王小龙
 * @Date: 2023/5/30 上午11:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {
    /**
     * 当前sql查询出和当前用户id关联的字段
     * 默认为 create_by_id
     *
     * @return 用户id字段
     */
    String userIdFiled() default "create_by_id";

    /**
     * 拦截的请求来源, 默认拦截所有
     * @return 请求来源数组
     */
    String[] requestSource() default {};
}