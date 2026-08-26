package com.lingyang.common.core.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;


/**
 * ID生成器工具类
 *
 * @author scrm
 */
public class IdUtils {
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 随机UUID
     */
    public static String fastUUID() {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID() {
        return UUID.fastUUID().toString(true);
    }


    public static Long nextId() {
        return SNOWFLAKE.nextId();
    }

    public static String getUuid(int length) {
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        return String.format("%0" + length + "d", hashCodeV);
    }

    /**

     * 生成字母开头的随机字符串

     *

     * @param length 字符串长度

     * @return 字母开头的随机字符串

     */

    public static String randomLetterString(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        // 生成第一个字母字符 (A-Z, a-z)
        char firstChar = (char) ('A' + Math.random() * 52);
        if (firstChar > 'Z') {
            firstChar = (char) (firstChar + ('a' - '['));
        }

        // 生成剩余的随机字符 (字母和数字)
        StringBuilder sb = new StringBuilder().append(firstChar);
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        for (int i = 1; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();

    }

    public static void main(String[] args) {
        String s = randomLetterString(36);
        System.out.println(s);
    }
}
