# AI 云平台运营系统 (ai-cloud-ops)

基于 Spring Boot 3 构建的云服务运营管理平台，集成火山引擎、腾讯云等云厂商能力，提供云资源管理、计费结算、订单管理、支付、用户运营等一站式云服务运营解决方案。

## 技术栈

| 类别 | 技术 |
|------|------|
| 基础框架 | Spring Boot 3.1.5、Java 17 |
| 数据访问 | MyBatis-Plus 3.5.3、Druid 1.2.18、MySQL 8.0 |
| 安全认证 | Spring Security、JWT |
| 缓存 | Redis |
| 消息队列 | RabbitMQ |
| API 文档 | SpringDoc OpenAPI 2.1.0 |
| 工具库 | Hutool 5.8.23、Fastjson2、EasyExcel |
| 支付 | 微信支付、支付宝 |
| 云服务 SDK | 火山引擎 SDK、腾讯云 SDK |

## 项目结构

```
├── ai-cloud-system              # 主应用模块
│   ├── src/main/java/com/lingyang/cloud/
│   │   ├── api/controller/      # API 接口层（PC/小程序/系统管理/回调）
│   │   ├── api/service/         # 业务服务层
│   │   ├── service/             # 核心服务层
│   │   ├── mapper/              # 数据访问层
│   │   ├── entity/              # 数据实体（68+）
│   │   ├── listener/queue/      # 消息队列消费者
│   │   ├── config/              # 配置类
│   │   ├── handler/             # 业务处理器
│   │   ├── utils/               # 工具类
│   │   └── AiCloudSystem.java   # 应用启动类
│   └── src/main/resources/
│       ├── application.yml      # 主配置
│       ├── application-{env}.yml # 多环境配置
│       └── mapper/*.xml         # MyBatis SQL 映射
│
└── lingyang-common              # 公共模块
    ├── lingyang-common-core     # 核心工具、常量、异常
    ├── lingyang-common-web      # Web 通用配置、拦截器、全局异常
    ├── lingyang-common-datasource # 数据源、分页、MyBatis 配置
    ├── lingyang-common-security # 安全认证、JWT、权限
    ├── lingyang-common-cache    # Redis 缓存封装
    ├── lingyang-common-log      # 日志配置与切面
    └── lingyang-common-http     # HTTP 调用封装
```

## 核心功能

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

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.9+

### 编译打包

```bash
# 编译整个项目
mvn clean install

# 打包主应用
mvn clean package -pl ai-cloud-system -am
```

### 运行应用

```bash
# 开发环境
mvn spring-boot:run -pl ai-cloud-system

# 或运行 jar
java -jar ai-cloud-system/target/ai-cloud-system.jar --spring.profiles.active=dev
```

### 配置文件

主要配置文件位于 `ai-cloud-system/src/main/resources/`：

| 文件 | 说明 |
|------|------|
| `application.yml` | 通用配置（SpringDoc、MyBatis-Plus、分页） |
| `application-dev.yml` | 开发环境（数据库、Redis、RabbitMQ） |
| `application-test.yml` | 测试环境 |
| `application-prod.yml` | 生产环境 |

### API 文档

启动后访问：http://localhost:8080/springdoc/index.html

## 数据库

- 逻辑删除：统一使用 `del_flag` 字段（1-删除，0-正常）
- 下划线转驼峰：开启自动映射
- 分页：使用 PageHelper + 自定义方言

## 模块统计

| 类型 | 数量 |
|------|------|
| Controller | 77+ |
| Entity | 68+ |
| Mapper XML | 14 |
| 消息队列消费者 | 15+ |

## 开发规范

- 接口分层：`api/controller`（对外）→ `api/service`（业务编排）→ `service`（核心逻辑）
- 数据模型：Query（查询参数）/ Edit（编辑参数）/ VO（返回视图）/ DTO（数据传输）
- 枚举管理：`enums` 包按业务域划分
- 消息消费：`listener/queue` 统一处理异步任务

## 作者

王小龙

## 许可证

本项目为私有项目，未经授权不得使用或分发。
