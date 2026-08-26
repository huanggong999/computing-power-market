package com.lingyang.common.core.utils;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/4 18:28
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";
    /**
     * www主域
     */
    public static final String WWW = "www.";

    /**
     * 是否为http(s)://开头
     *
     * @param link 链接
     * @return 结果
     */
    public static boolean isHttp(String link)
    {
        return StringUtils.startsWithAny(link, HTTP, HTTPS);
    }

    public static void requireNonNull(String permission, String errorMsg) {
        if (isEmpty(permission)) {
            throw new NullPointerException(errorMsg);
        }
    }
}
