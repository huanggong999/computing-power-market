# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 提供关于本代码仓库的行为规范与项目指引。

---

## 行为准则

**权衡：** 以下准则倾向于谨慎而非速度。对于简单任务，请自行判断。

### 1. 编码前思考

**不要假设。不要隐藏困惑。暴露权衡。**

在实现之前：
- 明确陈述你的假设。如果不确定，请提问。
- 如果存在多种理解方式，请呈现出来——不要默默选择。
- 如果存在更简单的方案，请说明。必要时提出反对意见。
- 如果有不清楚的地方，停下来。指出困惑之处。提问。

### 2. 简单优先

**最少代码解决问题。不做推测性实现。**

- 不添加超出需求的功能。
- 不为一次性代码创建抽象。
- 不添加未被要求的"灵活性"或"可配置性"。
- 不处理不可能发生的场景的错误。
- 如果你写了 200 行但 50 行就能解决，重写它。

问自己："资深工程师会觉得这过度复杂吗？"如果是，简化。

### 3. 精准修改

**只触碰必须修改的部分。只清理自己造成的混乱。**

编辑现有代码时：
- 不要"改进"相邻的代码、注释或格式。
- 不要重构没有问题的代码。
- 遵循现有风格，即使你自己会采用不同方式。
- 如果你注意到无关的死代码，提一下——不要删除它。

当你的修改产生孤立代码时：
- 删除你的修改导致不再使用的导入/变量/函数。
- 不要删除预先存在的死代码，除非被要求。

检验标准：每一行修改都应能直接追溯到用户的请求。

### 4. 目标驱动执行

**定义成功标准。循环验证直到通过。**

将任务转化为可验证的目标：
- "添加验证" → "为无效输入编写测试，然后让它们通过"
- "修复 bug" → "编写能复现问题的测试，然后让它通过"
- "重构 X" → "确保重构前后测试都能通过"

对于多步骤任务，简要说明计划：
```
1. [步骤] → 验证：[检查]
2. [步骤] → 验证：[检查]
3. [步骤] → 验证：[检查]
```

明确的成功标准能让你独立迭代。弱标准（"让它能跑起来"）需要不断澄清。

---

**这些准则有效体现在：** diff 中不必要修改更少、因过度复杂导致的重写更少，以及澄清问题出现在实现之前而非错误之后。

---

## 构建与运行

这是一个**无根 `pom.xml` 的多模块 Maven 项目**。两个顶级目录是独立的 Maven 构建：

- **`lingyang-common/`** — 公共库的父 POM（必须先构建）
- **`ai-cloud-system/`** — 主 Spring Boot 应用

常用命令：

```bash
# 1. 先构建公共库
cd lingyang-common && mvn clean install

# 2. 构建主应用
cd ai-cloud-system && mvn clean package

# 3. 本地运行（dev 环境，端口 8081）
cd ai-cloud-system && mvn spring-boot:run
```

本项目中**不存在测试**。没有 Docker、CI/CD 或数据库迁移工具（Flyway/Liquibase）。

## 项目概述

AI 云平台运营系统（ai-cloud-ops）是基于 Spring Boot 3 构建的云服务运营管理平台，集成火山引擎、腾讯云等云厂商能力，提供云资源管理、计费结算、订单管理、支付、用户运营等一站式云服务运营解决方案。

### 技术栈

| 类别 | 技术 |
|------|------|
| 基础框架 | Spring Boot 3.1.5、Java 17 |
| 数据访问 | MyBatis-Plus 3.5.3、Druid 1.2.18、MySQL 8.0 |
| 安全认证 | Spring Security、JWT |
| 缓存 | Redis |
| 消息队列 | RabbitMQ（注：实际使用自定义 Redis 缓存队列） |
| API 文档 | SpringDoc OpenAPI 2.1.0 |
| 工具库 | Hutool 5.8.23、Fastjson2、EasyExcel |
| 支付 | 微信支付、支付宝 |
| 云服务 SDK | 火山引擎 SDK、腾讯云 SDK |

### 核心功能

- **云资源管理**：ECS 实例、VPC、子网、EIP、安全组、云硬盘、镜像仓库、负载均衡、NAT 网关
- **容器服务**：VKE 集群、容器实例管理
- **计费结算**：账单生成、小时/日账单、自动结算、欠费处理
- **订单系统**：云资源购买、续费、变配、到期处理
- **支付中心**：微信支付、支付宝集成、退款处理
- **用户运营**：用户管理、实名认证、代金券、优惠券、活动管理
- **财务管理**：充值、提现、发票、授信管理
- **消息通知**：系统消息、公告、站内信
- **AI 服务**：AI 对话、飞连集成
- **系统管理**：角色权限、菜单、配置、日志

## 架构概览

### 模块结构

```
ai-cloud-system/               主 Spring Boot 3.1.5 应用（Java 17）
lingyang-common/               公共库父 POM
├── lingyang-common-core       基础实体、Result 包装、异常、工具类、SecurityContext
├── lingyang-common-web        WebConfig、Jackson 定制、BaseController、Excel 工具、隐私序列化器
├── lingyang-common-datasource MyBatis-Plus 配置、CustomMapper、BaseEntity、分页
├── lingyang-common-security   JWT、Spring Security 过滤器链、验证码、密码工具
├── lingyang-common-log        @Log AOP 注解与切面
├── lingyang-common-cache      Redis 缓存、缓存队列（基于 Redis 的异步消息队列）
└── lingyang-common-http       Forest HTTP 客户端封装
```

`ai-cloud-system` 依赖 `lingyang-common-web`、`lingyang-common-security`、`lingyang-common-datasource`、`lingyang-common-log` 和 `lingyang-common-cache`。

### 应用分层

主应用遵循以下约定：

```
api/controller/{pc|system|applets|callback}/   REST 控制器（4 个端点分组）
api/service/{pc|system|applets}/impl/           API 专属服务编排
service/impl/                                    核心业务逻辑
mapper/                                          MyBatis-Plus 映射器
entity/                                          数据库实体（全部继承 BaseEntity）
model/query/                                     查询参数对象
model/edit/                                      创建/更新参数对象
model/vo/                                        响应视图对象
model/dto/                                       内部数据传输对象
listener/queue/                                  异步缓存队列消费者
config/                                          Spring 配置类
voengine/                                        火山引擎云 SDK 集成
take/                                            定时任务
```

### 关键模式

**BaseEntity**（`lingyang-common-datasource`）：所有实体均继承此类。字段 `id`、`createBy`、`createById`、`createTime`、`updateBy`、`updateById`、`updateTime`、`delFlag` 通过 MyBatis-Plus `MetaObjectHandler`（`MybatisPlusConfig`）自动填充。逻辑删除使用 `delFlag`（1 = 已删除，0 = 正常）。

**CustomMapper**（`lingyang-common-datasource`）：所有映射器继承 `CustomMapper<T>`（继承 `BaseMapper<T>`），以获得 `batchInsert(Collection<T>)` 和 `batchUpdateById(Collection<T>)` 方法，默认批量大小为 500。

**BaseController**（`lingyang-common-web`）：泛型 CRUD 基类，类型参数为 `<S, QUERY, EDIT, LIST_VO, DETAIL_VO>`。提供默认的 `/page`、`/`、`/save`、`/update`、`/delete` 端点。大多数系统管理控制器继承此类。

**Result 包装**（`lingyang-common-core`）：所有控制器返回 `Result<T>` 或 `PageResult<T>`。使用 `Result.success(data)`、`Result.fail()`、`Result.error(msg)`。`Result.throwsError(...)` 抛出 `HttpServiceException`。

**Jackson 定制**（`MvcConfig`）：`Long` 值序列化为 `String` 以避免 JS 精度丢失。`BigDecimal` 使用自定义序列化器。`FAIL_ON_UNKNOWN_PROPERTIES` 已禁用。

**@Log 注解**（`lingyang-common-log`）：用于控制器方法进行 AOP 操作日志记录。配置项包括 `value`（标题）、`businessType`、`isSaveRequestData`、`isSaveResponseData`、`excludeParamNames`。

### 安全架构

基于 JWT 的无状态认证，使用 Spring Security 6。配置在 `SecurityProperty` 中（非传统的 `WebSecurityConfigurerAdapter`）。关键行为：

- `security.enabled=true` 激活过滤器链。
- `SecurityProperties` 配置白名单路径（`ignores`）、验证码（`check-urls`）、CORS、令牌过期时间。
- `MultipleSourceAuthenticationContextHandler` 在每个请求开始时将 `MultipleSourceAuthenticationToken` 设置到 `SecurityContext`。
- `CustomAuthenticationFilter` 验证请求头中的 JWT 令牌。
- `LoginFilter` 处理 `/login` 认证。
- `ValidateCodeFilter` / `ValidateCodeCheckFilter` 在配置的 URL 上强制验证码校验。
- `SecurityContext.getUserInfo()` 可在请求线程的任何位置返回当前 `LoginUserInfoDetail`。

### 异步处理：缓存队列

项目未使用 RabbitMQ，而是使用**自定义基于 Redis 的缓存队列**（`lingyang-common-cache`）：

- `CacheQueueService.addDelayQueue(String messageType, String messageContent, long delaySeconds)` 发布延迟消息。
- `CacheQueueConsumer` 接口：实现 `messageType()` 和 `invoke(String messageContent)`。
- 消费者是 Spring `@Service` Bean，由 `CacheQueueListenerFactory` 自动发现。
- 位于 `ai-cloud-system/src/main/java/com/lingyang/cloud/listener/queue/`。

### 定时任务

位于 `ai-cloud-system/src/main/java/com/lingyang/cloud/take/`：

- `CustomerBucketBillTask` — 每日存储计费（cron：`0 0 6 * * ?`）
- `MyTask` — 多个固定频率任务，处理订单结算、产品到期、AGIC 线路取消、余额不足短信

### 外部集成

| 服务 | SDK / 库 | 用途 |
|------|-----------|------|
| 火山引擎 | `volcengine-java-sdk-*` | ECS、VPC、EBS、VKE、CR、NAT、CLB、Billing、TOS、ARK Runtime |
| 支付宝 | `alipay-sdk-java` | 在线支付 |
| 微信支付 | `wechatpay-java`、`weixin-java-pay` | 支付处理 |
| 腾讯云 | `tencentcloud-sdk-java-faceid` | 实名认证 |
| 飞连 | 自定义 HTTP 客户端 | VPN / 网络产品管理 |

## 配置说明

- **Profiles**：`dev`（端口 8081，Swagger 启用）、`test`（端口 8081，Swagger 启用）、`prod`（端口 8082，Swagger 禁用）。`web` profile 始终包含。
- **MyBatis-Plus**：`mapper-locations: classpath*:mapper/*.xml`，`type-aliases-package: com.lingyang.cloud.entity.*`，逻辑删除字段 `delFlag`。
- **分页**：PageHelper 配合自定义 `PageDialect` 和 MySQL 方言。
- **文件上传**：火山引擎 TOS（对象存储）。
- **API 文档**：SpringDoc OpenAPI。启用时访问 `/springdoc/index.html`。

## 重要约定

- 项目**没有根 POM**。构建 `ai-cloud-system` 前必须先构建 `lingyang-common`。
- `Long` 类型的 ID 在 JSON 中序列化为字符串。
- PC API 控制器前缀为 `/pc/`，系统管理 `/system/`，小程序 API `/applets/`，回调 `/apiCallback/`。
- 不要硬编码敏感凭证；它们存放在各环境专属的 `application-{env}.yml` 文件中。
- 所有实体上的 `delFlag` 字段控制软删除。
- `PageQuery.PAGE_NO_NAME` 和 `PageQuery.PAGE_SIZE_NAME` 是标准的分页参数名称。

## 作者与许可

- **作者**：王小龙
- **许可**：本项目为私有项目，未经授权不得使用或分发。
