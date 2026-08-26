package com.lingyang.common.core.utils;

import com.lingyang.common.core.exception.MethodExecutionException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:37
 */
public class ClassUtils {

    private ClassUtils(){}

    /**
     * 获取实例
     * @param clazz 实例类型
     * @param args 构造器参数
     * @param <T> 实例
     * @return 实例
     * @throws MethodExecutionException 方法执行异常
     */
    public static <T> T newInstance(Class<T> clazz, Object... args) throws MethodExecutionException {
        try {
            if (args != null && args.length > 0) {
                Class<?>[] argsClass = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    argsClass[i] = args[i].getClass();
                }
                return clazz.getDeclaredConstructor(argsClass).newInstance(args);
            }
            return clazz.getDeclaredConstructor().newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new MethodExecutionException("newInstance Error：" + e.getMessage());
        } catch (Exception e) {
            throw new MethodExecutionException("newInstance Error：" + clazz.getCanonicalName(), e);
        }
    }

    public static <T extends Annotation> T getAnnotationMethodOrClass(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    public static <T extends Annotation> T getAnnotation(Method method, Class<T> annotationClass) {
        return method.getAnnotation(annotationClass);
    }
}
