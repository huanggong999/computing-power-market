package com.lingyang.common.web.service;

import com.lingyang.common.core.enums.HttpStatus;
import com.lingyang.common.core.exception.http.HttpParamsException;
import com.lingyang.common.core.exception.http.HttpRequestException;
import com.lingyang.common.core.model.page.PageQuery;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;

import java.util.List;

/**
 * @Description: 继承业务接口
 * @QUERY： 接收分页请求参数对象
 * @EDIT： 接收新增或更新请求参数对象
 * @LIST_VO: 响应分页或列表参数对象
 * @DETAIL_VO: 响应详情参数对象
 * @Author: 王小龙
 * @Date: 2023/8/9 15:46
 */
public interface BaseService<QUERY, EDIT, LIST_VO, DETAIL_VO> {

    default PageResult<LIST_VO> getPage(PageQuery<QUERY> page) {
//        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER).error();
        page.startPage();
        return PageResult.of(getList(page.getQuery()));
    }

    default List<LIST_VO> getList(QUERY query) {
        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER).error();
        return null;
    }

    default DETAIL_VO getDetail(Long id) {
        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER).error();
        return null;
    }

    default Boolean save(EDIT dto) {
        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER).error();
        return false;
    }

    default Boolean update(EDIT dto) {
        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER).error();
        return false;
    }

    default Boolean remove(Long id) {
        Result.buildError(HttpRequestException.class, HttpStatus.NOT_REQUEST_HANDLER);
        return false;
    }

    default void checkSaveParams(EDIT d) throws HttpParamsException {

    }

    default void checkUpdateParams(EDIT d) throws HttpParamsException {

    }

    default void checkRemove(Long id) throws HttpParamsException {

    }
}