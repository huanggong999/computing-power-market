# ai-cloud-system Linux 部署使用文档

本文档说明如何把 `console-main/ai-cloud-system` 这个 Spring Boot 3.1.5 项目编译、打包并在 Linux 服务器上部署运行。

> 本文档对应 `console-main/ai-cloud-system/deploy/` 目录下的脚本与配置。

---

## 1. 项目概览

- **应用名**：ai-cloud-system（AI 云平台运营系统）
- **基础框架**：Spring Boot 3.1.5、Java 17
- **持久化**：MySQL 8.0 + MyBatis-Plus 3.5.3
- **缓存**：Redis（同时承担自定义缓存队列）
- **集成**：火山引擎 SDK、腾讯云、支付宝、微信支付
- **构建产物**：Spring Boot fat jar，单 jar 部署

仓库结构（多模块，无根 POM）：

```
console-main/
├── lingyang-common/          # 公共库父 POM，必须先构建
│   ├── lingyang-common-core
│   ├── lingyang-common-web
│   ├── lingyang-common-datasource
│   ├── lingyang-common-security
│   ├── lingyang-common-log
│   ├── lingyang-common-cache
│   └── lingyang-common-http
└── ai-cloud-system/          # 主应用
    ├── pom.xml
    ├── src/
    └── deploy/               # 本文档对应的部署目录
```

---

## 2. 环境要求

### 2.1 开发机（用于打包）

| 项目 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | 编译运行均需 17，低版本不行 |
| Maven | 3.6+ | 仅用于命令行构建 |
| 网络 | 可访问 Maven 中央仓库 | 包含火山引擎、支付宝、微信支付等私有制品 |

> **重要**：`java -version` 显示 17 不代表 Maven 也用 17。请先执行 `mvn -version | head -3` 确认 `Java version: 17.x`。如果 Maven 实际用了 JDK 18/21/26 等更高版本，Lombok 注解处理器会失败，导致枚举类、日志类编译不过。修复方式：临时指定 `JAVA_HOME` 到 JDK 17，再跑 Maven。

### 2.2 Linux 服务器（运行环境）

| 项目 | 版本 | 说明 |
|------|------|------|
| 系统 | CentOS 7+ / Ubuntu 18+ / Anolis / 其它主流发行版 | 任何能跑 JDK 17 的 Linux |
| JDK | 17 | 必须 17，验证：`java -version` |
| MySQL | 8.0+ | 默认配置见 `application-prod.yml`，可外置覆盖 |
| Redis | 5.0+ | 默认配置见 `application-prod.yml`，可外置覆盖 |
| 出网 | 可访问火山引擎、支付宝、微信支付、回调域名 | 业务依赖外部服务 |
| 端口 | 默认 `8082`（prod），`8081`（dev/test） | 可通过环境变量覆盖 |

### 2.3 运行账户

不要求 root。建议用普通账户（例如 `appuser`）部署，便于权限隔离。

---

## 3. 一次性打包

### 3.1 构建公共库

```bash
cd console-main/lingyang-common
mvn clean install -DskipTests
```

**这一步必须先做**。`ai-cloud-system` 依赖 `lingyang-common-*`，公共库不在中央仓库，只有先 `install` 到本地仓库，主应用才能编译。

如果构建报枚举类/日志类 `找不到符号`，请先确认 `mvn -version` 里的 Java 版本是 17：

```bash
mvn -version | head -3
# Java version: 17.0.x, vendor: Eclipse Adoptium, runtime: .../temurin-17...
```

若不是 17，临时切换：

```bash
export JAVA_HOME=/path/to/jdk17
export PATH=$JAVA_HOME/bin:$PATH
```

### 3.2 构建主应用

```bash
cd console-main/ai-cloud-system
mvn clean package -DskipTests
```

成功后生成：

```
ai-cloud-system/target/ai-cloud-system.jar
```

这是 Spring Boot fat jar，含全部依赖。

### 3.3 验证 jar 可执行

可在开发机本地试跑（连得通 MySQL/Redis 才能完全启动，但能验证 jar 没坏）：

```bash
java -jar ai-cloud-system/target/ai-cloud-system.jar --spring.profiles.active=dev
```

`Ctrl+C` 退出。

---

## 4. 部署目录

部署目录位于 `console-main/ai-cloud-system/deploy/`，结构如下：

```
deploy/
├── bin/
│   ├── ai-cloud-system   # 单文件多命令脚本: start|stop|restart|status|logs
│   ├── start.sh          # 单独的启动脚本
│   ├── stop.sh           # 单独的停止脚本
│   └── restart.sh        # 单独的重启脚本
├── config/               # 外置配置（可选），覆盖 jar 内同名 application-*.yml
├── lib/                  # 放 ai-cloud-system.jar 的目录
├── logs/                 # 运行时生成 stdout.log、heapdump、业务日志
└── README.md
```

文件说明：

| 路径 | 用途 |
|------|------|
| `bin/ai-cloud-system` | 推荐使用的统一入口，五个子命令 |
| `bin/start.sh` / `stop.sh` / `restart.sh` | 等价的单独脚本，老式 init 风格 |
| `config/application-prod.yml` | 可选，外置覆盖 jar 内配置 |
| `lib/ai-cloud-system.jar` | 你打好的 fat jar，需自己放进去 |
| `logs/stdout.log` | nohup 重定向的标准输出/错误，启动失败必看 |
| `logs/heapdump.hprof` | OOM 时自动生成 |
| `logs/<业务日志>` | 由 jar 内 `logback-spring.xml` 决定 |
| `ai-cloud-system.pid` | 启动后写入的 pid 文件，stop 时读取 |

---

## 5. 上传到 Linux 服务器

```bash
# 在开发机
cp ai-cloud-system/target/ai-cloud-system.jar \
   ai-cloud-system/deploy/lib/

cd ai-cloud-system
tar czf ai-cloud-system-deploy.tgz deploy/

scp ai-cloud-system-deploy.tgz appuser@<server>:/opt/

# 在 Linux 服务器
ssh appuser@<server>
cd /opt
tar xzf ai-cloud-system-deploy.tgz
cd deploy/bin
chmod +x ai-cloud-system start.sh stop.sh restart.sh
```

最终服务器路径：`/opt/deploy/`（你也可以放在 `/data/app/ai-cloud-system/` 等任意路径）。

---

## 6. 运行控制

### 6.1 推荐：单文件多命令

```bash
cd /opt/deploy/bin

./ai-cloud-system start     # 启动
./ai-cloud-system status    # 查看是否在跑
./ai-cloud-system logs      # tail -f stdout
./ai-cloud-system stop      # 优雅停止（SIGTERM，30s 后 SIGKILL）
./ai-cloud-system restart   # 重启
```

### 6.2 老式独立脚本

```bash
cd /opt/deploy/bin

./start.sh      # 启动
./stop.sh       # 停止
./restart.sh    # 重启
```

### 6.3 启动状态判断

- `start` 完成后：
  - 控制台输出 `[OK] ai-cloud-system 已启动 (pid=...)`
  - `deploy/ai-cloud-system.pid` 文件被写入
  - `deploy/logs/stdout.log` 开始有内容

- `status` 退出码：
  - `0`：在运行
  - `3`：未运行

`status` 的退出码可被 systemd / 监控脚本利用。

---

## 7. 可调参数（环境变量）

启动脚本读取以下环境变量，留空则用默认值：

| 变量 | 默认 | 含义 |
|------|------|------|
| `PROFILE` | `prod` | Spring profile，可选 `dev` / `test` / `prod` |
| `SERVER_PORT` | `8082` | HTTP 端口 |
| `JAVA_OPTS` | `-server -Xms1g -Xmx2g -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=<logs>/heapdump.hprof -Dfile.encoding=UTF-8` | JVM 参数 |

举例：

```bash
PROFILE=prod SERVER_PORT=8888 JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC" \
  ./ai-cloud-system start
```

如果想固化一组参数，可以在 `bin/` 同级建 `env.sh`，脚本里 `source env.sh` 引入；或写到运行账户的 `~/.bashrc`。

---

## 8. 配置覆盖（外置 application-*.yml）

### 8.1 工作机制

启动脚本带了：

```
-Dspring.profiles.active=${PROFILE}
-Dspring.config.additional-location=file:${CONFIG_DIR}/
```

这是 Spring Boot 标准的"外部化配置"机制：

- `config/` 下的 `application-prod.yml` 优先级 **高于** jar 内的 `application-prod.yml`
- 你只需要写要覆盖的 key，不必整份复制

### 8.2 常见覆盖场景

#### 8.2.1 换数据库地址

```yaml
# /opt/deploy/config/application-prod.yml
spring:
  datasource:
    url: jdbc:mysql://10.0.0.10:3306/ai_cloud?useUnicode=true&characterEncoding=utf8
    username: ai_cloud
    password: <new-password>
  data:
    redis:
      host: 10.0.0.11
      port: 6379
      password: <new-redis-password>
```

#### 8.2.2 换端口

可以用环境变量 `SERVER_PORT=8888 ./ai-cloud-system start`，也可以写到外置配置：

```yaml
server:
  port: 8888
```

#### 8.2.3 换支付回调地址 / 火山引擎 AK

```yaml
vol-engine:
  access-key: <new-ak>
  access-secret: <new-sk>

alipay:
  notify_url: https://yourdomain.com/api/apiCallback/onlinePayCallback/ali_pay_notify
  return_url: https://yourdomain.com/api/apiCallback/onlinePayCallback/ali_pay_return
```

### 8.3 优先级（高→低）

1. JVM 参数：`-Dxxx=yyy`
2. 环境变量：`SERVER_PORT` 等（被 Spring 自动识别）
3. `config/application-{profile}.yml`（外置）
4. jar 内 `application-{profile}.yml`
5. jar 内 `application.yml`

---

## 9. Profile 说明

| Profile | 端口 | Swagger | 用途 |
|---------|------|---------|------|
| `dev` | 8081 | 启用 | 本地开发 |
| `test` | 8081 | 启用 | 测试环境 |
| `prod` | 8082 | 禁用 | 生产环境（默认） |

`web` profile 始终自动启用，无需关心。

启动时传 `PROFILE=test ./ai-cloud-system start` 即可切换。

---

## 10. 安全提醒（非常重要）

⚠ **`application-prod.yml` 内已经写死了真实凭证**，包括：

- MySQL 用户名/密码
- Redis 密码
- 火山引擎 AK / SK
- 支付宝应用私钥（企业账户）
- 微信支付商户号 / API Key
- 飞连密钥

打出来的 `ai-cloud-system.jar` **包含上述所有凭证**。这意味着：

1. 不要把这个 jar 提交到任何公开仓库
2. 不要把这个 jar 给到不可信的第三方
3. 服务器上的 `lib/ai-cloud-system.jar` 文件权限至少改成 `600`：
   ```bash
   chmod 600 /opt/deploy/lib/ai-cloud-system.jar
   ```
4. 如果是给到不同租户/不同环境的部署，强烈建议：
   - 把 prod 配置中的敏感字段换成占位符（例如 `${MYSQL_PASSWORD}`）
   - 把真实值放到 `deploy/config/application-prod.yml` 里
   - `chmod 600 deploy/config/application-prod.yml`

---

## 11. 健康检查

应用本身没有暴露 actuator endpoint，可以使用以下方式检查存活：

### 11.1 端口探活

```bash
ss -ltnp | grep 8082
# 或
netstat -anp 2>/dev/null | grep 8082
```

### 11.2 HTTP 响应

```bash
curl -sI http://127.0.0.1:8082/login
# 期望：HTTP/1.1 200 OK 或 405（method not allowed）
```

### 11.3 进程检查

```bash
/opt/deploy/bin/ai-cloud-system status
echo "exit=$?"
# 在跑：exit=0
# 不在跑：exit=3
```

---

## 12. 日志

| 文件 | 内容 |
|------|------|
| `logs/stdout.log` | nohup 重定向的 stdout/stderr，**启动失败先看这个** |
| `logs/<业务日志>.log` | 由 jar 内 `logback-spring.xml` 配置生成 |
| `logs/heapdump.hprof` | OOM 时自动 dump，定位内存泄漏用 |

实时看：

```bash
./ai-cloud-system logs
# 等价于
tail -f /opt/deploy/logs/stdout.log
```

---

## 13. 升级流程

```bash
# 1. 在开发机重新打包
cd console-main/ai-cloud-system
mvn clean package -DskipTests

# 2. 上传新 jar
scp target/ai-cloud-system.jar appuser@<server>:/opt/deploy/lib/

# 3. 在服务器重启
ssh appuser@<server>
cd /opt/deploy/bin
./ai-cloud-system restart
./ai-cloud-system logs
```

如果想保留旧版回滚能力，上传前做个备份：

```bash
ssh appuser@<server> "cp /opt/deploy/lib/ai-cloud-system.jar /opt/deploy/lib/ai-cloud-system.jar.$(date +%Y%m%d_%H%M%S).bak"
```

---

## 14. 开机自启（可选）

### 14.1 systemd（推荐）

`/etc/systemd/system/ai-cloud-system.service`：

```ini
[Unit]
Description=AI Cloud System
After=network.target mysql.service redis.service

[Service]
Type=forking
User=appuser
Group=appuser
WorkingDirectory=/opt/deploy/bin
ExecStart=/opt/deploy/bin/ai-cloud-system start
ExecStop=/opt/deploy/bin/ai-cloud-system stop
ExecReload=/opt/deploy/bin/ai-cloud-system restart
PIDFile=/opt/deploy/ai-cloud-system.pid
Restart=on-failure
RestartSec=10
Environment=PROFILE=prod
Environment=SERVER_PORT=8082
# 如果系统默认 java 不是 17，把 PATH 显式写进来：
# Environment=PATH=/usr/lib/jvm/java-17/bin:/usr/local/bin:/usr/bin:/bin

[Install]
WantedBy=multi-user.target
```

启用：

```bash
sudo systemctl daemon-reload
sudo systemctl enable ai-cloud-system
sudo systemctl start ai-cloud-system
sudo systemctl status ai-cloud-system
journalctl -u ai-cloud-system -f
```

### 14.2 crontab @reboot

简单但不带监控：

```bash
crontab -e
# 添加
@reboot /opt/deploy/bin/ai-cloud-system start
```

---

## 15. 常见问题

### 15.1 `java: command not found`

未装 JDK 17 或 PATH 没有 java。

```bash
# CentOS / Anolis
sudo yum install -y java-17-openjdk

# Ubuntu / Debian
sudo apt-get install -y openjdk-17-jdk

# 验证
java -version
```

### 15.2 启动后立刻退出

看 `logs/stdout.log`，几乎都是：

- 数据库连接失败：检查 `application-prod.yml` 的 url、账号、密码、网络
- Redis 连接失败：同上
- 端口被占用：`ss -ltnp | grep 8082`，或换端口

### 15.3 端口被占

```bash
SERVER_PORT=8888 ./ai-cloud-system start
```

### 15.4 `Caused by: java.lang.UnsupportedClassVersionError`

JDK 版本不对，必须 17。`update-alternatives --config java` 切到 17。

### 15.5 `Permission denied`

```bash
chmod +x /opt/deploy/bin/ai-cloud-system /opt/deploy/bin/*.sh
```

### 15.6 想看 SQL

prod 默认 `mybatis-plus` 用 stdout 打印 SQL，已经在 `logs/stdout.log` 里。生产想关掉：

```yaml
# config/application-prod.yml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
```

### 15.7 时区不对

```yaml
# config/application-prod.yml
spring:
  jackson:
    time-zone: Asia/Shanghai
```

JVM 层面也可以加 `-Duser.timezone=Asia/Shanghai` 到 `JAVA_OPTS`。

---

## 16. 卸载

```bash
cd /opt/deploy/bin
./ai-cloud-system stop

# 如果用了 systemd
sudo systemctl disable --now ai-cloud-system
sudo rm /etc/systemd/system/ai-cloud-system.service
sudo systemctl daemon-reload

# 删除部署目录
rm -rf /opt/deploy
```

---

## 17. 快速参考

```bash
# 打包（开发机）
cd console-main/lingyang-common && mvn clean install -DskipTests
cd ../ai-cloud-system          && mvn clean package -DskipTests

# 部署（服务器）
mkdir -p /opt/deploy/lib
cp ai-cloud-system.jar /opt/deploy/lib/
cd /opt/deploy/bin

# 启动 / 停止 / 重启 / 状态 / 日志
./ai-cloud-system start
./ai-cloud-system stop
./ai-cloud-system restart
./ai-cloud-system status
./ai-cloud-system logs
```

---

**作者**：王小龙
**适用版本**：ai-cloud-system 1.0-SNAPSHOT
**最后更新**：2026-06-20
