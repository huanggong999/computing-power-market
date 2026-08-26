# lingyang-common
集成Springboot，SpringMVC，Mybatis-plus，等基础功能模块

# 1、插件版本
``
JDK 
17
``

``
SpringBoot 
3.1.2
``

``
mysql 
8.0.30
``

``
mybatis-plus
3.5.3.1
``

``
mybatis-plus
3.5.3.1
``

``
druid
1.2.18
``

``
fastjson2
2.0.39
``

``
pagehelper
5.3.1
``

``
commons.io
2.11.0
``

``
hutool
5.8.6
``

``
google.zxing.core
3.3.3
``

``
jjwt
0.9.1
``

``
jaxb-api
2.3.1
``

``
transmittable-thread-local
2.2.0
``

``
kaptcha
2.3.3
``

``
forest
1.5.32
``

``
springdoc-openapi
2.1.0
``
# 2、模块说明
[lingyang-common-cache](lingyang-common-cache)  - 缓存

[lingyang-common-core](lingyang-common-core) - 核心代码，常用工具

[lingyang-common-datasource](lingyang-common-datasource) - 数据源处理

[lingyang-common-http](lingyang-common-http) - 外部HTTP调用

[lingyang-common-log](lingyang-common-log) - 日志处理

[lingyang-common-web](lingyang-common-web) - web请求拦截，过滤处理

# 3、功能说明
3.1、登陆
````java
/**
 * 前端调用：
 *  |- 请求路径：/login（默认：可在yml文件中 security.login-url配置）
 *  |- 请求方式：post
 *  |- 请求参数：json
 */
import com.lingyang.common.web.security.user.AuthenticationInterface;

public class AdminAuthService implements AuthenticationInterface<用户信息, 登陆参数>{

    /**
     * 处理登陆
     * @param loginParam 登陆参数
     * @return 登陆用户信息
     */
    @Override
    public LoginUser<用户信息> login(登陆参数 loginParam) {
        // todo 登陆逻辑
        return LoginUser.builder(用户信息.class)
                // 用户id
                .userId(1L)
                .userPhone("手机号")
                .userName("用户名")
                .nickName("用户别名")
                // 权限字符集合，配合 com.lingyang.common.web.security.annotation.PreAuthorize注解使用
                .permissions(Set.of("*"))
                // 角色结婚
                .roleList(List.of());
    }

    /**
     * 获取登陆参数类型
     * @return 登陆参数类型
     */
    @Override
    public Class<登陆参数> getLoginParamClass() {
        return 登陆参数.class;
    }

    /**
     * 处理对应的请求来源登陆逻辑
     * @return RequestSource
     */
    @Override
    public RequestSource getRequestSource() {
        return RequestSource.PC_ADMIN;
    }
}
````
3.2、验证码
```java
/**
 * 前端调用：
 *  |- 请求路径：/code（默认：可在yml文件中 security.captcha.api_url配置）
 *  |- 请求方式：get
 *  |- 请求参数：
 *          type 类型
 *          key  参数
 */
import com.lingyang.common.web.security.validate.ValidateCodeHandlerInterface;

public class SmsValidateCodeHandler implements ValidateCodeHandlerInterface {

    /**
     * 生成验证码逻辑
     * @param param 参数，可为空
     * @return 验证码结果数据
     */
    @Override
    public ValidateCodeResult handlerCode(String param) {
        // todo 处理验证码生成逻辑
        return null;
    }

    /**
     * 对应处理的验证码类型
     * 已默认提供 ValidateCodeType.MATH 和 ValidateCodeType.CHAR 的type处理
     * @return ValidateCodeType
     */
    @Override
    public ValidateCodeType type() {
        return null;
    }
}
```