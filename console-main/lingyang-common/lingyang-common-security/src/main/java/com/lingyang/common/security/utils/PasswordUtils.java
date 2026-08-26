package com.lingyang.common.security.utils;

import com.lingyang.common.core.utils.SpringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/8 14:50
 */
public class PasswordUtils {

    private static final PasswordEncoder PASSWORD_ENCODER;

    static {
        PASSWORD_ENCODER = SpringUtils.getBean(PasswordEncoder.class);
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 如果编码后的原始密码与存储中的编码密码匹配，则为 true
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
