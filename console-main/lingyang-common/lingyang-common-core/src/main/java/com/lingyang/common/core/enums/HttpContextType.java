package com.lingyang.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/11/2 16:58
 */
@AllArgsConstructor
@Getter
public enum HttpContextType {

    /**
     * 标准表单编码，当action为get时候，浏览器用x-www-form-urlencoded的编码方式把form数据转换成一个字串（name1=value1&amp;name2=value2…）
     */
    FORM_URLENCODED("application/x-www-form-urlencoded"),
    /**
     * 文件上传编码，浏览器会把整个表单以控件为单位分割，并为每个部分加上Content-Disposition，并加上分割符(boundary)
     */
    MULTIPART("multipart/form-data"),
    /**
     * Rest请求JSON编码
     */
    JSON("application/json"),
    /**
     * Rest请求XML编码
     */
    XML("application/xml"),
    /**
     * text/plain编码
     */
    TEXT_PLAIN("text/plain"),
    /**
     * Rest请求text/xml编码
     */
    TEXT_XML("text/xml"),
    /**
     * text/html编码
     */
    TEXT_HTML("text/html"),

    /**
     * image/jpeg编码
     */
    IMAGE_JPEG("image/jpeg"),
    /**
     * application/octet-stream编码
     */
    OCTET_STREAM("application/octet-stream");

    private final String value;

}
