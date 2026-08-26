package com.lingyang.common.core.utils;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;


public class UrlUtils {

    private UrlUtils(){}

    /**
     * 判断url是否与规则配置：
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     * @param patternList 匹配规则集合
     * @param str 路径
     * @return true/false
     */
    public static boolean matches(String[] patternList, String str) {
        if (ObjectUtils.isEmpty(patternList) || StringUtils.isEmpty(str)) {
            return false;
        }
        for (String pattern : patternList) {
            if (StringUtils.isEmpty(pattern)) {
                return false;
            }
            if (isMatch(pattern, str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断url是否与规则配置：
     * ? 表示单个字符;
     * * 表示一层路径内的任意字符串，不可跨层级;
     * ** 表示任意层路径;
     * @param pattern 匹配规则
     * @param url 路径
     * @return true/false
     */
    public static boolean isMatch(String pattern, String url) {
        AntPathMatcher matcher = new AntPathMatcher();
        return matcher.match(pattern, url);
    }
}
