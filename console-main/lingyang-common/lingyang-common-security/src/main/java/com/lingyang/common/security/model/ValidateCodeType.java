package com.lingyang.common.security.model;

import com.lingyang.common.core.utils.StringUtils;
import lombok.Getter;

import java.util.Objects;

/**
 * @Description: 验证码类型
 * @Author: 王小龙
 * @Date: 2023/8/14 17:46
 */
public interface ValidateCodeType {
    /**
     * 数组计算
     */
    Type MATH = Type.create("math");
    /**
     * 字符验证
     */
    Type CHAR = Type.create("char");


    @Getter
    class Type {
        String typeName;
        private Type(String typeName) {
            Objects.requireNonNull(typeName, "typeName is null");
            this.typeName = typeName;
        }

        public static Type create(String typeName) {
            return new Type(typeName);
        }

        @Override
        public int hashCode() {
            return this.typeName.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) {
                return false;
            }
            if (o instanceof Type type) {
                return StringUtils.equals(this.typeName, type.typeName);
            }
            return false;
        }
    }
}