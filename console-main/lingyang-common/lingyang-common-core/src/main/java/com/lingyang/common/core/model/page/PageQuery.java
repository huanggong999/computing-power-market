package com.lingyang.common.core.model.page;

import com.github.pagehelper.PageHelper;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.core.utils.ServletUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/9 15:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageQuery<T> extends BasePage {
    @Serial
    private static final long serialVersionUID = 7556937811173387110L;

    public static final String PAGE_NO_NAME = "pageNo";
    public static final String PAGE_SIZE_NAME = "pageSize";
    private T query;

    public static <T> PageQuery<T> build() {
        return build(null);
    }

    public static <T> PageQuery<T> build(T query) {
        Integer pageNo = ServletUtils.getParameterToInt(PAGE_NO_NAME);
        Integer pageSize = ServletUtils.getParameterToInt(PAGE_SIZE_NAME);
        return build(pageNo, pageSize, query);
    }

    public static <T> PageQuery<T> build(Integer pageNo, Integer pageSize) {
        return build(pageNo, pageSize, null);
    }


    public static <T> PageQuery<T> build(Integer pageNo, Integer pageSize, T query) {
        PageQuery<T> pageDTO = new PageQuery<>();
        pageDTO.setPageNo(pageNo == null ? 1 : pageNo);
        pageDTO.setPageSize(pageSize == null ? 10 : pageSize);
        pageDTO.setQuery(query);
        return pageDTO;
    }

    public <S> S getQuery(Class<S> queryClass) {
        return BeanUtils.copyBean(query, queryClass);
    }

    /**
     * 开启分页
     * 当前方法执行完成之后，后面只有第一条sql执行分页，直到执行PageResult.of() 或  PageHelper.clearPage() 方法后，其他sql才能执行分页
     */
    public void startPage() {
        PageHelper.startPage(getPageNo(), getPageSize());
    }
}
