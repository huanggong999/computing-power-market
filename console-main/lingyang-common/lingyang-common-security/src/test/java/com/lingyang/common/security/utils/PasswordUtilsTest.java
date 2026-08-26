package com.lingyang.common.security.utils;

import com.lingyang.common.core.utils.SpringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = PasswordUtilsTest.TestConfig.class)
class PasswordUtilsTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void encryptPassword() {
        String encrypted = PasswordUtils.encryptPassword("123456");
        assertNotNull(encrypted);
        System.out.println(encrypted);
        assertTrue(passwordEncoder.matches("123456", encrypted));
    }

    @Test
    void matchesPassword() {
        String encrypted = PasswordUtils.encryptPassword("123456");
        assertTrue(PasswordUtils.matchesPassword("123456", encrypted));
        assertFalse(PasswordUtils.matchesPassword("wrong", encrypted));
    }

    @Configuration
    static class TestConfig {

        @Bean
        public SpringUtils springUtils() {
            return new SpringUtils();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
