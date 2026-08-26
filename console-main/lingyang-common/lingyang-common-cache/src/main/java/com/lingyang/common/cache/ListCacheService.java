package com.lingyang.common.cache;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/22 14:16
 */
public interface ListCacheService {

    boolean valueExistList(Object value);

    /**
     * 在相同key中添加一个值，这个key是个列表
     *
     * @param value 值
     */
     void addValue(Object value);

    /**
     * 在相同key中添加多个值，这个key是个列表
     *
     * @param values 值
     */
    void addValues(Object... values);

    /**
     * 删除key中的值， 这个key是个列表
     *
     * @param value 值
     */
    void delValue(Object value);

    /**
     * 删除key中的值， 这个key是个列表
     *
     * @param values 值：一个或多个
     */
    void delValues(List<?> values);

    @SuppressWarnings("unchecked")
    <T> List<T> getAllValue();
}
