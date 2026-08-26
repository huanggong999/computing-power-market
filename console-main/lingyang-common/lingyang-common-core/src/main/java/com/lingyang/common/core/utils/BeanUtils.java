package com.lingyang.common.core.utils;

import cn.hutool.core.bean.BeanUtil;
import com.lingyang.common.core.exception.MethodExecutionException;
import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/8 17:09
 */
public class BeanUtils extends BeanUtil {
    /**
     * Bean属性复制工具方法。
     *
     * @param dest 目标对象
     * @param src  源对象
     */
    public static void copyBeanProp(Object dest, Object src) {
        copyProperties(src, dest);
    }

    /**
     * Bean属性复制
     *
     * @param src       源对象
     * @param destClass 目标对象Class
     * @param <T>       目标对象类型
     * @return 目标对象
     */
    public static <T> T copyBean(Object src, Class<T> destClass) {
        return copyBean(src, destClass, null);
    }

    /**
     * Bean属性复制
     *
     * @param src        源对象
     * @param destClass  目标对象Class
     * @param <T>        目标对象类型
     * @param defaultObJ 源对象为空时，返回默认对象
     * @return 目标对象
     */
    public static <T> T copyBean(Object src, Class<T> destClass, T defaultObJ) {
        if (ObjectUtils.isEmpty(src)) {
            return defaultObJ;
        }
        T bean = ClassUtils.newInstance(destClass);
        copyProperties(src, bean);
        return bean;
    }

    /**
     * copy数组
     *
     * @param source   源数据
     * @param tagClass 目标元素类型
     * @param <S>      源数据类型
     * @param <T>      目标数据类型
     * @return list
     */
    public static <S, T> List<T> copyList(List<S> source, Class<T> tagClass) {
        return copyList(source, tagClass, null);
    }

    /**
     * copy数组
     *
     * @param source       源数据
     * @param tagClass     目标元素类型
     * @param defaultValue 源数据为空时，返回默认对象
     * @param <S>          元数据类型
     * @param <T>          目标数据类型
     * @return list
     */
    public static <S, T> List<T> copyList(Collection<S> source, Class<T> tagClass, List<T> defaultValue) {
        if (source == null) {
            return defaultValue;
        }
        return copyToList(source, tagClass);
    }

    @SuppressWarnings(value = "unchecked")
    public static <T> T getProperties(Object bean, String filedName, T defaultValue) {
        try {
            defaultValue = (T) getField(bean, filedName).get(bean);
        } catch (Exception ignored) {

        }
        return defaultValue;
    }

    public static void updateProperties(Object bean, String fieldName, Object value) {
        try {
            Field field = getField(bean, fieldName);
            field.set(bean, value);
        } catch (Exception e) {
            throw new MethodExecutionException("update bean properties error", e);
        }
    }

    public static Field getField(Object bean, String fieldName) {
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            throw new MethodExecutionException("get bean field  error", e);
        }
    }
}
