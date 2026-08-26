package com.lingyang.common.log.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/17 13:57
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogTitle {

    String title() default "";
}
