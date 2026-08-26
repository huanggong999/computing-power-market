package com.lingyang.common.datasource.model;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * @Description: 自定义mapper
 * @Author: 王小龙
 * @Date: 2024/3/28 16:50
 */
public interface CustomMapper<T> extends BaseMapper<T> {
    int BATCH_FLUSH_ZIE = 500;

    /**
     * 批量插入
     *
     * @param entityList 数据列表
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean batchInsert(Collection<T> entityList) {
        return batchInsert(entityList, BATCH_FLUSH_ZIE);
    }

    /**
     * 批量插入
     *
     * @param entityList 数据列表
     * @param batchSize  刷新大小，避免内除溢出
     * @return ignore
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean batchInsert(Collection<T> entityList, int batchSize) {
        return Db.saveBatch(entityList, batchSize);
    }

    /**
     * 通过id，批量更新
     * @param entityList 实体列表，必须声明 {@link com.baomidou.mybatisplus.annotation.TableId}注解
     * @return true/false
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean batchUpdateById(Collection<T> entityList) {
        return batchUpdateById(entityList, BATCH_FLUSH_ZIE);
    }

    /**
     * 通过id，批量更新
     * @param entityList 实体列表，必须声明 {@link com.baomidou.mybatisplus.annotation.TableId}注解
     * @param batchSize 刷新大小，避免内除溢出
     * @return true/false
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean batchUpdateById(Collection<T> entityList, int batchSize) {
        return Db.updateBatchById(entityList, batchSize);
    }
}