package com.lingyang.common.security.validate;

import com.lingyang.common.core.security.ValidateCodeResult;
import com.lingyang.common.security.model.ValidateCodeType;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2023/8/14 18:10
 */
public interface ValidateCodeHandlerInterface<T> {
    /**
     * 处理验证码
     * param：
     * <p>
     * 将请求中的query参数，转换成对应{@link ValidateCodeHandlerInterface#jsonClass()}类型的对象，
     *
     * @param param {@link ValidateCodeHandlerInterface#jsonClass()}的类型为字符串，param为json字符串，其它则是该类型对象
     *              </p>
     * @return 处理结果
     */
    ValidateCodeResult handlerCode(T param);

    /**
     * 验证码类型
     * <p>
     * {@link ValidateCodeType}
     * </p>
     *
     * @return 类型
     */
    ValidateCodeType.Type type();

    /**
     * <p>
     * {@link ValidateCodeHandlerInterface#handlerCode(T)}
     * </p>
     *
     * @return 参数类型
     */
    Class<? extends T> jsonClass();
}