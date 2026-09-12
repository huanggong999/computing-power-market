# ai-cloud-system Linux 部署

## 目录约定

```
deploy/
├── bin/
│   ├── ai-cloud-system   # 单文件五命令脚本：start | stop | restart | status | logs
│   ├── start.sh          # 也可单独使用
│   ├── stop.sh
│   └── restart.sh
├── lib/                  # 放打好的 ai-cloud-system.jar
├── config/               # 可选：外置 application-*.yml，会覆盖 jar 内同名配置
└── logs/                 # stdout.log、heapdump、业务日志
```

## 一次性准备

**环境要求（开发机）**：

- JDK 17（必须。注意：本机 `java -version` 显示 17，不代表 Maven 也用 17）
- Maven 3.6+

**打包前确认 Maven 使用 JDK 17**：

```bash
mvn -version | head -3
# 应看到 Java version: 17.x
```

如果 `mvn -version` 里显示 Java 18/21/26 等更高版本，Lombok 注解处理器会失败，导致枚举类/日志类编译不过。请临时设置：

```bash
# macOS 示例（路径以本机实际 JDK 17 为准）
export JAVA_HOME=/Users/liujiaxing/Library/Java/JavaVirtualMachines/temurin-17.0.18/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
mvn -version  # 确认是 17
```

**开发机打包**：

```bash
cd lingyang-common && mvn clean install
cd ../ai-cloud-system && mvn clean package
```

会得到 `ai-cloud-system/target/ai-cloud-system.jar`（Spring Boot fat jar，已包含全部依赖）。

把这个 jar 复制到 Linux 机器的 `deploy/lib/`，或直接上传 `deploy/` 目录（已包含启停脚本、可选外置配置目录）：

```bash
# 方式一：直接上传整个 deploy 目录
tar czf deploy.tgz deploy/
scp deploy.tgz user@server:/opt/
ssh user@server "cd /opt && tar xzf deploy.tgz && cd deploy/bin && ./ai-cloud-system start"

# 方式二：只传 jar（对方服务器已有脚本时）
scp ai-cloud-system/target/ai-cloud-system.jar user@server:/opt/deploy/lib/
ssh user@server "cd /opt/deploy/bin && ./ai-cloud-system restart"
```

## Linux 服务器要求

- JDK 17（必须，项目就是 Java 17 编译的）
  ```bash
  java -version  # 应输出 17.x
  ```
- 可访问 MySQL 8.0（地址、账号已写死在 jar 内 `application-prod.yml`）
- 可访问 Redis（同上）
- 出网到火山引擎 / 支付宝 / 微信支付等回调地址

## 启动 / 停止

```bash
cd /opt/deploy/bin
./ai-cloud-system start     # 启动
./ai-cloud-system status    # 查看是否在跑
./ai-cloud-system logs      # tail -f stdout
./ai-cloud-system stop      # 优雅停止（SIGTERM，30s 后 SIGKILL）
./ai-cloud-system restart   # 重启
```

或者用三个独立脚本：

```bash
./start.sh
./stop.sh
./restart.sh
```

## 可调环境变量

```bash
PROFILE=prod          # 默认 prod；可改 test
SERVER_PORT=8082      # 默认 8082；prod profile 也是 8082
JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC ..."  # 默认 -Xms1g -Xmx2g
```

例如：

```bash
PROFILE=prod SERVER_PORT=8888 JAVA_OPTS="-Xms2g -Xmx4g" ./ai-cloud-system start
```

## 外置配置（可选）

`config/` 下的 `application-prod.yml` 会**覆盖** jar 内同名 key（Spring Boot 外部化配置）。
当你需要在 Linux 上换 MySQL 地址、关 Swagger、改回调 URL 时，**只放要改的 key**，不需要整份复制：

```yaml
# config/application-prod.yml
server:
  port: 8082
spring:
  datasource:
    url: jdbc:mysql://your-prod-mysql:3306/ai_cloud?useUnicode=true&characterEncoding=utf8
    username: ai_cloud
    password: change-me
  data:
    redis:
      host: your-prod-redis
      password: change-me
```

## 健康检查

应用本身没有暴露 actuator endpoint，可以用端口探活：

```bash
ss -ltnp | grep 8082
curl -sI http://127.0.0.1:8082/login
```

## 日志

- `logs/stdout.log` —— nohup 重定向的 stdout/stderr，启动失败先看这个
- 业务日志按 jar 内 `logback-spring.xml` 配置生成，通常也在 `logs/` 下
- `logs/heapdump.hprof` —— OOM 时自动生成，方便排查

## 常见问题

1. **`java: command not found`**：装 JDK 17，配置 `JAVA_HOME` 和 `PATH`
2. **端口被占**：`SERVER_PORT=8888 ./ai-cloud-system start`
3. **连不上 MySQL/Redis**：检查 `application-prod.yml` 的地址和服务器到 RDS 的网络连通性（`telnet host port`）
4. **启动后进程立刻退出**：看 `logs/stdout.log`，多半是配置 / 数据库 / Redis 问题
