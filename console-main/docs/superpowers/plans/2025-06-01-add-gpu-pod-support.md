# GPU Pod 支持功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现 GPU Pod 实例生命周期管理、定时关机、日志查看和 SSH 访问功能，对接外部 GPU Pod 服务。

**Architecture:** 采用分层架构，Controller 处理 HTTP 请求和参数校验，Service 处理业务逻辑和对外部 GPU Pod 服务的调用，DTO/VO 负责数据转换。通过 HTTP Client 与外部 GPU Pod 服务通信，使用 JWT Token 进行认证。

**Tech Stack:** Java 17, Spring Boot 3.1.5, Spring Security 6, MyBatis-Plus, Volcengine SDK, HttpClient, JWT

---

## 前置依赖

在开始实现前，确保以下数据库表已存在（由之前的 SQL 迁移创建）：
- `gpu_resource` - GPU 资源主表
- `gpu_resource_spec` - GPU 规格定义表
- `gpu_resource_price` - GPU 资源价格表
- `gpu_resource_stock` - GPU 资源库存表
- `gpu_region` - GPU 地区配置表
- `gpu_zone` - GPU 专区配置表

---

## 模块设计

### 目录结构

```
ai-cloud-system/src/main/java/com/lingyang/cloud/
├── api/controller/pc/GpuPodController.java          # GPU Pod 控制器
├── service/
│   ├── GpuPodService.java                         # 服务接口
│   └── impl/GpuPodServiceImpl.java                # 服务实现
├── client/
│   ├── GpuPodApiClient.java                       # GPU Pod API 客户端
│   ├── dto/
│   │   ├── CreateInstanceRequest.java             # 创建实例请求
│   │   ├── InstanceResponse.java                  # 实例响应
│   │   └── ...                                    # 其他 DTO
│   └── config/
│       └── GpuPodProperties.java                  # GPU Pod 配置属性
├── entity/
│   └── SysCustomerGpuPodEntity.java               # GPU Pod 实体（新增）
├── mapper/
│   └── SysCustomerGpuPodMapper.java               # GPU Pod Mapper（新增）
└── model/
    ├── dto/
    │   ├── CreateGpuPodDTO.java                   # 创建实例 DTO
    │   ├── SetShutdownScheduleDTO.java            # 设置定时关机 DTO
    │   └── ...
    ├── vo/
    │   ├── GpuPodInstanceVO.java                  # 实例详情 VO
    │   ├── SshInfoVO.java                         # SSH 信息 VO
    │   └── ShutdownScheduleVO.java                # 定时关机 VO
    └── query/
        └── GpuPodInstanceQuery.java                 # 实例查询参数

ai-cloud-system/src/main/resources/
└── mapper/
    └── SysCustomerGpuPodMapper.xml                # GPU Pod Mapper XML
```

---

## 数据库表设计

### sys_customer_gpu_pod 表

```sql
CREATE TABLE IF NOT EXISTS sys_customer_gpu_pod (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    customer_id         BIGINT NOT NULL COMMENT '客户ID',
    order_id            BIGINT COMMENT '关联订单ID',
    order_source_uid    VARCHAR(64) COMMENT '订单资源UID',
    
    -- 实例基本信息
    instance_id         VARCHAR(64) NOT NULL COMMENT 'GPU Pod实例ID（外部服务返回）',
    instance_name       VARCHAR(128) COMMENT '实例名称',
    status              VARCHAR(32) NOT NULL COMMENT '状态: running, stopped, starting, stopping, creating, error, releasing, released',
    
    -- 区域信息
    region_code         VARCHAR(32) NOT NULL COMMENT '地区编码',
    region_name         VARCHAR(64) COMMENT '地区名称',
    zone_code           VARCHAR(32) COMMENT '可用区编码',
    zone_name           VARCHAR(64) COMMENT '可用区名称',
    
    -- GPU规格
    gpu_model           VARCHAR(64) NOT NULL COMMENT 'GPU型号',
    gpu_count           INT NOT NULL DEFAULT 1 COMMENT 'GPU数量',
    gpu_memory          VARCHAR(32) COMMENT 'GPU显存',
    
    -- 资源配置
    cpu_cores           INT NOT NULL COMMENT 'CPU核数',
    memory_size         VARCHAR(32) NOT NULL COMMENT '内存大小',
    system_disk_size    INT COMMENT '系统盘大小(GB)',
    data_disk_size      INT COMMENT '数据盘大小(GB)',
    
    -- 镜像信息
    image_url           VARCHAR(256) COMMENT '镜像地址',
    
    -- 计费信息
    billing_mode        VARCHAR(32) NOT NULL COMMENT '计费模式: on_demand, hourly, daily, weekly, monthly, yearly',
    billing_status      VARCHAR(32) COMMENT '计费状态',
    price_per_hour      DECIMAL(10, 2) COMMENT '每小时价格',
    
    -- 时间信息
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    start_time          DATETIME COMMENT '启动时间',
    stop_time           DATETIME COMMENT '停止时间',
    release_time        DATETIME COMMENT '释放时间',
    expire_time         DATETIME COMMENT '到期时间',
    
    -- 定时关机
    scheduled_shutdown_time DATETIME COMMENT '定时关机时间',
    
    -- SSH信息（加密存储）
    ssh_host            VARCHAR(256) COMMENT 'SSH主机',
    ssh_port            INT DEFAULT 22 COMMENT 'SSH端口',
    ssh_username        VARCHAR(64) COMMENT 'SSH用户名',
    ssh_password        VARCHAR(256) COMMENT 'SSH密码（加密）',
    
    -- 扩展信息（JSON格式存储额外字段）
    extra_info          TEXT COMMENT '扩展信息JSON',
    
    -- 标准字段
    create_by           VARCHAR(64),
    create_by_id        BIGINT,
    update_by           VARCHAR(64),
    update_by_id        BIGINT,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag            TINYINT(1) DEFAULT 0,
    
    INDEX idx_customer_id (customer_id),
    INDEX idx_instance_id (instance_id),
    INDEX idx_status (status),
    INDEX idx_region_code (region_code),
    INDEX idx_create_time (create_time),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户GPU Pod实例表';
```

---

## Task 1: 创建数据库表和实体类

**Files:**
- Create: `ai-cloud-system/src/main/resources/db/migration/V2025_06_01_001__add_sys_customer_gpu_pod.sql`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/entity/SysCustomerGpuPodEntity.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/mapper/SysCustomerGpuPodMapper.java`
- Create: `ai-cloud-system/src/main/resources/mapper/SysCustomerGpuPodMapper.xml`

- [ ] **Step 1: 创建数据库迁移脚本**

```sql
-- 客户GPU Pod实例表
CREATE TABLE IF NOT EXISTS sys_customer_gpu_pod (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    customer_id         BIGINT NOT NULL COMMENT '客户ID',
    order_id            BIGINT COMMENT '关联订单ID',
    order_source_uid    VARCHAR(64) COMMENT '订单资源UID',
    instance_id         VARCHAR(64) NOT NULL COMMENT 'GPU Pod实例ID',
    instance_name       VARCHAR(128) COMMENT '实例名称',
    status              VARCHAR(32) NOT NULL COMMENT '状态',
    region_code         VARCHAR(32) NOT NULL COMMENT '地区编码',
    region_name         VARCHAR(64) COMMENT '地区名称',
    zone_code           VARCHAR(32) COMMENT '可用区编码',
    zone_name           VARCHAR(64) COMMENT '可用区名称',
    gpu_model           VARCHAR(64) NOT NULL COMMENT 'GPU型号',
    gpu_count           INT NOT NULL DEFAULT 1 COMMENT 'GPU数量',
    gpu_memory          VARCHAR(32) COMMENT 'GPU显存',
    cpu_cores           INT NOT NULL COMMENT 'CPU核数',
    memory_size         VARCHAR(32) NOT NULL COMMENT '内存大小',
    system_disk_size    INT COMMENT '系统盘大小(GB)',
    data_disk_size      INT COMMENT '数据盘大小(GB)',
    image_url           VARCHAR(256) COMMENT '镜像地址',
    billing_mode        VARCHAR(32) NOT NULL COMMENT '计费模式',
    billing_status      VARCHAR(32) COMMENT '计费状态',
    price_per_hour      DECIMAL(10, 2) COMMENT '每小时价格',
    create_time         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    start_time          DATETIME COMMENT '启动时间',
    stop_time           DATETIME COMMENT '停止时间',
    release_time        DATETIME COMMENT '释放时间',
    expire_time         DATETIME COMMENT '到期时间',
    scheduled_shutdown_time DATETIME COMMENT '定时关机时间',
    ssh_host            VARCHAR(256) COMMENT 'SSH主机',
    ssh_port            INT DEFAULT 22 COMMENT 'SSH端口',
    ssh_username        VARCHAR(64) COMMENT 'SSH用户名',
    ssh_password        VARCHAR(256) COMMENT 'SSH密码（加密）',
    extra_info          TEXT COMMENT '扩展信息JSON',
    create_by           VARCHAR(64),
    create_by_id        BIGINT,
    update_by           VARCHAR(64),
    update_by_id        BIGINT,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag            TINYINT(1) DEFAULT 0,
    INDEX idx_customer_id (customer_id),
    INDEX idx_instance_id (instance_id),
    INDEX idx_status (status),
    INDEX idx_region_code (region_code),
    INDEX idx_create_time (create_time),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户GPU Pod实例表';
```

- [ ] **Step 2: 创建实体类**

```java
package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客户GPU Pod实例表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_customer_gpu_pod")
@Schema(description = "客户GPU Pod实例")
public class SysCustomerGpuPodEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableField("customer_id")
    @Schema(description = "客户ID")
    private Long customerId;

    @TableField("order_id")
    @Schema(description = "关联订单ID")
    private Long orderId;

    @TableField("order_source_uid")
    @Schema(description = "订单资源UID")
    private String orderSourceUid;

    @TableField("instance_id")
    @Schema(description = "GPU Pod实例ID")
    private String instanceId;

    @TableField("instance_name")
    @Schema(description = "实例名称")
    private String instanceName;

    @TableField("status")
    @Schema(description = "状态: running, stopped, starting, stopping, creating, error, releasing, released")
    private String status;

    @TableField("region_code")
    @Schema(description = "地区编码")
    private String regionCode;

    @TableField("region_name")
    @Schema(description = "地区名称")
    private String regionName;

    @TableField("zone_code")
    @Schema(description = "可用区编码")
    private String zoneCode;

    @TableField("zone_name")
    @Schema(description = "可用区名称")
    private String zoneName;

    @TableField("gpu_model")
    @Schema(description = "GPU型号")
    private String gpuModel;

    @TableField("gpu_count")
    @Schema(description = "GPU数量")
    private Integer gpuCount;

    @TableField("gpu_memory")
    @Schema(description = "GPU显存")
    private String gpuMemory;

    @TableField("cpu_cores")
    @Schema(description = "CPU核数")
    private Integer cpuCores;

    @TableField("memory_size")
    @Schema(description = "内存大小")
    private String memorySize;

    @TableField("system_disk_size")
    @Schema(description = "系统盘大小(GB)")
    private Integer systemDiskSize;

    @TableField("data_disk_size")
    @Schema(description = "数据盘大小(GB)")
    private Integer dataDiskSize;

    @TableField("image_url")
    @Schema(description = "镜像地址")
    private String imageUrl;

    @TableField("billing_mode")
    @Schema(description = "计费模式")
    private String billingMode;

    @TableField("billing_status")
    @Schema(description = "计费状态")
    private String billingStatus;

    @TableField("price_per_hour")
    @Schema(description = "每小时价格")
    private BigDecimal pricePerHour;

    @TableField("create_time")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField("start_time")
    @Schema(description = "启动时间")
    private LocalDateTime startTime;

    @TableField("stop_time")
    @Schema(description = "停止时间")
    private LocalDateTime stopTime;

    @TableField("release_time")
    @Schema(description = "释放时间")
    private LocalDateTime releaseTime;

    @TableField("expire_time")
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @TableField("scheduled_shutdown_time")
    @Schema(description = "定时关机时间")
    private LocalDateTime scheduledShutdownTime;

    @TableField("ssh_host")
    @Schema(description = "SSH主机")
    private String sshHost;

    @TableField("ssh_port")
    @Schema(description = "SSH端口")
    private Integer sshPort;

    @TableField("ssh_username")
    @Schema(description = "SSH用户名")
    private String sshUsername;

    @TableField("ssh_password")
    @Schema(description = "SSH密码（加密）")
    private String sshPassword;

    @TableField("extra_info")
    @Schema(description = "扩展信息JSON")
    private String extraInfo;
}
```

- [ ] **Step 3: 创建 Mapper 接口**

```java
package com.lingyang.cloud.mapper;

import com.lingyang.cloud.entity.SysCustomerGpuPodEntity;
import com.lingyang.common.datasource.mapper.CustomMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户GPU Pod实例Mapper
 */
@Mapper
public interface SysCustomerGpuPodMapper extends CustomMapper<SysCustomerGpuPodEntity> {
}
```

- [ ] **Step 4: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.lingyang.cloud.mapper.SysCustomerGpuPodMapper">

    <resultMap id="BaseResultMap" type="com.lingyang.cloud.entity.SysCustomerGpuPodEntity">
        <id column="id" property="id"/>
        <result column="customer_id" property="customerId"/>
        <result column="order_id" property="orderId"/>
        <result column="order_source_uid" property="orderSourceUid"/>
        <result column="instance_id" property="instanceId"/>
        <result column="instance_name" property="instanceName"/>
        <result column="status" property="status"/>
        <result column="region_code" property="regionCode"/>
        <result column="region_name" property="regionName"/>
        <result column="zone_code" property="zoneCode"/>
        <result column="zone_name" property="zoneName"/>
        <result column="gpu_model" property="gpuModel"/>
        <result column="gpu_count" property="gpuCount"/>
        <result column="gpu_memory" property="gpuMemory"/>
        <result column="cpu_cores" property="cpuCores"/>
        <result column="memory_size" property="memorySize"/>
        <result column="system_disk_size" property="systemDiskSize"/>
        <result column="data_disk_size" property="dataDiskSize"/>
        <result column="image_url" property="imageUrl"/>
        <result column="billing_mode" property="billingMode"/>
        <result column="billing_status" property="billingStatus"/>
        <result column="price_per_hour" property="pricePerHour"/>
        <result column="create_time" property="createTime"/>
        <result column="start_time" property="startTime"/>
        <result column="stop_time" property="stopTime"/>
        <result column="release_time" property="releaseTime"/>
        <result column="expire_time" property="expireTime"/>
        <result column="scheduled_shutdown_time" property="scheduledShutdownTime"/>
        <result column="ssh_host" property="sshHost"/>
        <result column="ssh_port" property="sshPort"/>
        <result column="ssh_username" property="sshUsername"/>
        <result column="ssh_password" property="sshPassword"/>
        <result column="extra_info" property="extraInfo"/>
        <result column="create_by" property="createBy"/>
        <result column="create_by_id" property="createById"/>
        <result column="update_by" property="updateBy"/>
        <result column="update_by_id" property="updateById"/>
        <result column="update_time" property="updateTime"/>
        <result column="del_flag" property="delFlag"/>
    </resultMap>

</mapper>
```

---

## Task 2: GPU Pod API 客户端

**Files:**
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/client/config/GpuPodProperties.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/client/GpuPodApiClient.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/*.java` (多个 DTO)

- [ ] **Step 1: 创建配置属性类**

```java
package com.lingyang.cloud.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GPU Pod 服务配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "gpu-pod")
public class GpuPodProperties {

    /**
     * GPU Pod 服务基础URL
     */
    private String baseUrl = "http://124.174.46.14:8000";

    /**
     * API 版本
     */
    private String apiVersion = "v1";

    /**
     * 连接超时（毫秒）
     */
    private int connectTimeout = 10000;

    /**
     * 读取超时（毫秒）
     */
    private int readTimeout = 30000;

    /**
     * JWT Token
     */
    private String jwtToken;
}
```

- [ ] **Step 2: 创建请求/响应 DTO**

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 创建实例请求
 */
@Data
public class CreateInstanceRequest {
    private String tenantId;
    private String tenantName;
    private GpuSpec gpuSpec;
    private String image;
    private Billing billing;
    private Resource resource;
    private String podName;
    private String region;

    @Data
    public static class GpuSpec {
        private String model;
        private int count;
    }

    @Data
    public static class Billing {
        private String mode;
        private Integer duration;
    }

    @Data
    public static class Resource {
        private String cpu;
        private String memory;
        private String systemDisk;
        private String dataDisk;
    }
}
```

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 实例响应
 */
@Data
public class InstanceResponse {
    private boolean success;
    private String instanceId;
    private String instanceName;
    private String message;
    private GpuInfo gpuInfo;
    private BillingInfo billingInfo;
    private String status;
    private String region;
    private String zone;
    private SshInfo sshInfo;

    @Data
    public static class GpuInfo {
        private String model;
        private int count;
        private String memory;
    }

    @Data
    public static class BillingInfo {
        private String gpuModel;
        private int gpuCount;
        private String billingMode;
        private BigDecimal unitPrice;
        private BigDecimal discountUnitPrice;
        private BigDecimal totalCost;
        private BigDecimal discountTotalCost;
        private String currency;
        private String unit;
    }

    @Data
    public static class SshInfo {
        private String host;
        private int port;
        private String username;
        private String password;
        private String command;
    }
}
```

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

import java.util.List;

/**
 * 实例列表响应
 */
@Data
public class InstanceListResponse {
    private int total;
    private List<InstanceResponse> instances;
}
```

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

/**
 * 定时关机请求
 */
@Data
public class ShutdownScheduleRequest {
    private String shutdownTime;  // ISO 8601格式，null表示取消
}
```

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

/**
 * 定时关机响应
 */
@Data
public class ShutdownScheduleResponse {
    private boolean hasSchedule;
    private String shutdownTime;
    private Long remainingTime;  // 剩余时间（秒）
}
```

- [ ] **Step 3: 创建 API 客户端**

```java
package com.lingyang.cloud.client;

import com.alibaba.fastjson2.JSON;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.client.dto.*;
import com.lingyang.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GPU Pod API 客户端
 */
@Slf4j
@Component
public class GpuPodApiClient {

    @Autowired
    private GpuPodProperties properties;

    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        // 可以在这里配置连接池、超时等
    }

    /**
     * 创建实例
     */
    public InstanceResponse createInstance(CreateInstanceRequest request) {
        String url = buildUrl("/api/{version}/gpu/pod/create");
        Map<String, String> pathVars = Collections.singletonMap("version", properties.getApiVersion());
        
        HttpHeaders headers = buildHeaders();
        HttpEntity<CreateInstanceRequest> entity = new HttpEntity<>(request, headers);
        
        log.info("Creating GPU Pod instance, tenant: {}", request.getTenantId());
        ResponseEntity<InstanceResponse> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, InstanceResponse.class, pathVars);
        
        return response.getBody();
    }

    /**
     * 获取实例详情
     */
    public InstanceResponse getInstance(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}");
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("version", properties.getApiVersion());
        pathVars.put("instanceId", instanceId);
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("tenant_id", tenantId);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<InstanceResponse> response = restTemplate.exchange(
            builder.buildAndExpand(pathVars).toUri(), HttpMethod.GET, entity, InstanceResponse.class);
        
        return response.getBody();
    }

    /**
     * 获取实例列表
     */
    public InstanceListResponse listInstances(String tenantId, Map<String, Object> params) {
        String url = buildUrl("/api/{version}/instances");
        Map<String, String> pathVars = Collections.singletonMap("version", properties.getApiVersion());
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("tenant_id", tenantId);
        
        // 添加分页和筛选参数
        if (params != null) {
            params.forEach((key, value) -> {
                if (value != null) {
                    builder.queryParam(key, value);
                }
            });
        }
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<InstanceListResponse> response = restTemplate.exchange(
            builder.buildAndExpand(pathVars).toUri(), HttpMethod.GET, entity, InstanceListResponse.class);
        
        return response.getBody();
    }

    /**
     * 启动实例
     */
    public void startInstance(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/start");
        executeInstanceAction(url, instanceId, tenantId, "start");
    }

    /**
     * 停止实例
     */
    public void stopInstance(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/stop");
        executeInstanceAction(url, instanceId, tenantId, "stop");
    }

    /**
     * 重启实例
     */
    public void restartInstance(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/restart");
        executeInstanceAction(url, instanceId, tenantId, "restart");
    }

    /**
     * 释放实例
     */
    public void releaseInstance(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/release");
        executeInstanceAction(url, instanceId, tenantId, "release");
    }

    /**
     * 获取实例日志
     */
    public String getInstanceLogs(String instanceId, String tenantId, int tailLines) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/logs");
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("version", properties.getApiVersion());
        pathVars.put("instanceId", instanceId);
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("tenant_id", tenantId)
            .queryParam("tail_lines", tailLines);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            builder.buildAndExpand(pathVars).toUri(), HttpMethod.GET, entity, String.class);
        
        return response.getBody();
    }

    /**
     * 设置定时关机
     */
    public void setShutdownSchedule(String instanceId, String tenantId, ShutdownScheduleRequest request) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/shutdown-schedule");
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("version", properties.getApiVersion());
        pathVars.put("instanceId", instanceId);
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("tenant_id", tenantId);
        
        HttpEntity<ShutdownScheduleRequest> entity = new HttpEntity<>(request, headers);
        
        restTemplate.exchange(builder.buildAndExpand(pathVars).toUri(), 
            HttpMethod.POST, entity, Void.class);
    }

    /**
     * 获取定时关机状态
     */
    public ShutdownScheduleResponse getShutdownSchedule(String instanceId, String tenantId) {
        String url = buildUrl("/api/{version}/instances/{instanceId}/shutdown-schedule");
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("version", properties.getApiVersion());
        pathVars.put("instanceId", instanceId);
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url)
            .queryParam("tenant_id", tenantId);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        ResponseEntity<ShutdownScheduleResponse> response = restTemplate.exchange(
            builder.buildAndExpand(pathVars).toUri(), HttpMethod.GET, entity, ShutdownScheduleResponse.class);
        
        return response.getBody();
    }

    // 私有辅助方法

    private String buildUrl(String path) {
        return properties.getBaseUrl() + path;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        // 添加 JWT Token
        if (StringUtils.isNotBlank(properties.getJwtToken())) {
            headers.setBearerAuth(properties.getJwtToken());
        }
        
        return headers;
    }

    private void executeInstanceAction(String urlTemplate, String instanceId, String tenantId, String action) {
        Map<String, String> pathVars = new HashMap<>();
        pathVars.put("version", properties.getApiVersion());
        pathVars.put("instanceId", instanceId);
        
        HttpHeaders headers = buildHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlTemplate)
            .queryParam("tenant_id", tenantId);
        
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        
        log.info("Executing {} action for instance: {}", action, instanceId);
        restTemplate.exchange(builder.buildAndExpand(pathVars).toUri(), 
            HttpMethod.POST, entity, Void.class);
    }
}
```

---

## Task 3: 创建 DTO 和 VO

**Files:**
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/dto/CreateGpuPodDTO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/dto/SetShutdownScheduleDTO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/dto/SetNameDTO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/vo/GpuPodInstanceVO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/vo/SshInfoVO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/vo/ShutdownScheduleVO.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/model/query/GpuPodInstanceQuery.java`

- [ ] **Step 1: 创建请求 DTO**

```java
package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建 GPU Pod 实例 DTO
 */
@Data
@Schema(description = "创建GPU Pod实例请求")
public class CreateGpuPodDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "区域不能为空")
    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String region;

    @Schema(description = "可用区编码")
    private String zone;

    @NotNull(message = "GPU规格不能为空")
    @Schema(description = "GPU规格", requiredMode = Schema.RequiredMode.REQUIRED)
    private GpuSpec gpuSpec;

    @NotBlank(message = "镜像地址不能为空")
    @Schema(description = "镜像地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String image;

    @NotNull(message = "计费配置不能为空")
    @Schema(description = "计费配置", requiredMode = Schema.RequiredMode.REQUIRED)
    private Billing billing;

    @Schema(description = "资源配置")
    private Resource resource;

    @Schema(description = "自定义Pod名称")
    private String podName;

    @Data
    @Schema(description = "GPU规格")
    public static class GpuSpec {
        @NotBlank(message = "GPU型号不能为空")
        @Schema(description = "GPU型号", requiredMode = Schema.RequiredMode.REQUIRED)
        private String model;

        @Schema(description = "GPU数量", defaultValue = "1")
        private int count = 1;
    }

    @Data
    @Schema(description = "计费配置")
    public static class Billing {
        @NotBlank(message = "计费模式不能为空")
        @Schema(description = "计费模式: on_demand, hourly, daily, weekly, monthly, yearly", requiredMode = Schema.RequiredMode.REQUIRED)
        private String mode;

        @Schema(description = "购买时长（包周期模式必填）")
        private Integer duration;
    }

    @Data
    @Schema(description = "资源配置")
    public static class Resource {
        @Schema(description = "CPU核数", defaultValue = "4")
        private String cpu;

        @Schema(description = "内存大小", defaultValue = "16Gi")
        private String memory;

        @Schema(description = "系统盘大小", defaultValue = "30Gi")
        private String systemDisk;

        @Schema(description = "数据盘大小", defaultValue = "50Gi")
        private String dataDisk;
    }
}
```

```java
package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设置定时关机 DTO
 */
@Data
@Schema(description = "设置定时关机请求")
public class SetShutdownScheduleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "关机时间（ISO 8601格式，如：2026-05-27T22:00:00），null表示取消", 
            example = "2026-05-27T22:00:00")
    private String shutdownTime;
}
```

```java
package com.lingyang.cloud.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 设置实例名称 DTO
 */
@Data
@Schema(description = "设置实例名称请求")
public class SetNameDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "名称不能为空")
    @Schema(description = "实例名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
```

- [ ] **Step 2: 创建响应 VO**

```java
package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * GPU Pod 实例详情 VO
 */
@Data
@Schema(description = "GPU Pod实例详情")
public class GpuPodInstanceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "实例ID")
    private String id;

    @Schema(description = "实例名称")
    private String name;

    @Schema(description = "区域编码")
    private String region;

    @Schema(description = "区域名称")
    private String regionName;

    @Schema(description = "可用区编码")
    private String zone;

    @Schema(description = "可用区名称")
    private String zoneName;

    @Schema(description = "状态: running, stopped, starting, stopping, creating, error, releasing, released")
    private String status;

    @Schema(description = "状态中文")
    private String statusText;

    @Schema(description = "GPU型号")
    private String gpuType;

    @Schema(description = "GPU数量")
    private Integer gpuCount;

    @Schema(description = "GPU显存")
    private String gpuMemory;

    @Schema(description = "CPU核数")
    private Integer cpuCores;

    @Schema(description = "内存大小")
    private String memory;

    @Schema(description = "系统盘大小(GB)")
    private Integer systemDisk;

    @Schema(description = "数据盘大小(GB)")
    private Integer dataDisk;

    @Schema(description = "镜像地址")
    private String imageUrl;

    @Schema(description = "计费类型")
    private String billingType;

    @Schema(description = "计费类型中文")
    private String billingTypeText;

    @Schema(description = "每小时价格")
    private BigDecimal pricePerHour;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "启动时间")
    private LocalDateTime startTime;

    @Schema(description = "停止时间")
    private LocalDateTime stopTime;

    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Schema(description = "定时关机时间")
    private LocalDateTime scheduledShutdownTime;

    @Schema(description = "SSH信息")
    private SshInfoVO sshInfo;

    @Schema(description = "健康状态")
    private HealthStatus healthStatus;

    @Data
    public static class HealthStatus {
        private String status;  // normal, warning, error
        private Double cpuUsage;
        private Double memoryUsage;
        private Double gpuUsage;
        private String message;
    }
}
```

```java
package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * SSH 信息 VO
 */
@Data
@Schema(description = "SSH连接信息")
public class SshInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主机地址")
    private String host;

    @Schema(description = "端口号")
    private Integer port;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码（明文，前端需要安全处理）")
    private String password;

    @Schema(description = "SSH连接命令示例")
    private String command;
}
```

```java
package com.lingyang.cloud.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 定时关机状态 VO
 */
@Data
@Schema(description = "定时关机状态")
public class ShutdownScheduleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "是否有定时关机任务")
    private Boolean hasSchedule;

    @Schema(description = "关机时间（ISO 8601格式）")
    private String shutdownTime;

    @Schema(description = "剩余时间（秒）")
    private Long remainingTime;
}
```

- [ ] **Step 3: 创建查询参数**

```java
package com.lingyang.cloud.model.query;

import com.lingyang.common.datasource.model.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * GPU Pod 实例查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "GPU Pod实例查询参数")
public class GpuPodInstanceQuery extends PageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "实例名称（模糊查询）")
    private String name;

    @Schema(description = "状态: running, stopped, starting, stopping, creating, error, releasing, released")
    private String status;

    @Schema(description = "区域编码")
    private String regionCode;

    @Schema(description = "GPU型号")
    private String gpuModel;

    @Schema(description = "计费模式")
    private String billingMode;
}
```

---

## Task 4: 创建 Service 层

**Files:**
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/service/GpuPodService.java`
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuPodServiceImpl.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.lingyang.cloud.service;

import com.lingyang.cloud.model.dto.CreateGpuPodDTO;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.cloud.model.dto.SetShutdownScheduleDTO;
import com.lingyang.cloud.model.query.GpuPodInstanceQuery;
import com.lingyang.cloud.model.vo.GpuPodInstanceVO;
import com.lingyang.cloud.model.vo.ShutdownScheduleVO;
import com.lingyang.cloud.model.vo.SshInfoVO;
import com.lingyang.common.core.page.PageResult;

/**
 * GPU Pod 服务接口
 */
public interface GpuPodService {

    /**
     * 获取实例列表
     */
    PageResult<GpuPodInstanceVO> listInstances(GpuPodInstanceQuery query);

    /**
     * 创建实例
     */
    GpuPodInstanceVO createInstance(CreateGpuPodDTO dto);

    /**
     * 获取实例详情
     */
    GpuPodInstanceVO getInstance(String id);

    /**
     * 启动实例
     */
    void startInstance(String id);

    /**
     * 停止实例
     */
    void stopInstance(String id);

    /**
     * 重启实例
     */
    void restartInstance(String id);

    /**
     * 释放实例
     */
    void releaseInstance(String id);

    /**
     * 重置密码
     */
    String resetPassword(String id);

    /**
     * 设置实例名称
     */
    void setInstanceName(String id, SetNameDTO dto);

    /**
     * 设置定时关机
     */
    void setShutdownSchedule(String id, SetShutdownScheduleDTO dto);

    /**
     * 获取定时关机状态
     */
    ShutdownScheduleVO getShutdownSchedule(String id);

    /**
     * 获取SSH信息
     */
    SshInfoVO getSshInfo(String id);

    /**
     * 获取实例日志
     */
    String getInstanceLogs(String id, int tailLines);
}
```

- [ ] **Step 2: 创建 Service 实现**

由于 Service 实现代码较长，这里展示核心结构，完整实现需要补充：

```java
package com.lingyang.cloud.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.dto.*;
import com.lingyang.cloud.entity.SysCustomerGpuPodEntity;
import com.lingyang.cloud.mapper.SysCustomerGpuPodMapper;
import com.lingyang.cloud.model.dto.CreateGpuPodDTO;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.cloud.model.dto.SetShutdownScheduleDTO;
import com.lingyang.cloud.model.query.GpuPodInstanceQuery;
import com.lingyang.cloud.model.vo.GpuPodInstanceVO;
import com.lingyang.cloud.model.vo.ShutdownScheduleVO;
import com.lingyang.cloud.model.vo.SshInfoVO;
import com.lingyang.cloud.service.GpuPodService;
import com.lingyang.common.core.exception.ServiceException;
import com.lingyang.common.core.page.PageResult;
import com.lingyang.common.security.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GPU Pod 服务实现
 */
@Slf4j
@Service
public class GpuPodServiceImpl implements GpuPodService {

    @Autowired
    private SysCustomerGpuPodMapper gpuPodMapper;

    @Autowired
    private GpuPodApiClient gpuPodApiClient;

    @Override
    public PageResult<GpuPodInstanceVO> listInstances(GpuPodInstanceQuery query) {
        Long customerId = SecurityUtils.getCustomerId();
        
        // 构建查询条件
        var wrapper = Wrappers.<SysCustomerGpuPodEntity>lambdaQuery()
            .eq(SysCustomerGpuPodEntity::getCustomerId, customerId)
            .eq(SysCustomerGpuPodEntity::getDelFlag, 0)
            .like(StringUtils.isNotBlank(query.getName()), SysCustomerGpuPodEntity::getInstanceName, query.getName())
            .eq(StringUtils.isNotBlank(query.getStatus()), SysCustomerGpuPodEntity::getStatus, query.getStatus())
            .eq(StringUtils.isNotBlank(query.getRegionCode()), SysCustomerGpuPodEntity::getRegionCode, query.getRegionCode())
            .eq(StringUtils.isNotBlank(query.getGpuModel()), SysCustomerGpuPodEntity::getGpuModel, query.getGpuModel())
            .eq(StringUtils.isNotBlank(query.getBillingMode()), SysCustomerGpuPodEntity::getBillingMode, query.getBillingMode())
            .orderByDesc(SysCustomerGpuPodEntity::getCreateTime);
        
        // 执行分页查询
        var page = gpuPodMapper.selectPage(query.page(), wrapper);
        
        // 转换为VO
        List<GpuPodInstanceVO> records = page.getRecords().stream()
            .map(this::convertToVO)
            .collect(Collectors.toList());
        
        return PageResult.of(records, page.getTotal());
    }

    @Override
    public GpuPodInstanceVO createInstance(CreateGpuPodDTO dto) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        // 构建创建请求
        CreateInstanceRequest request = new CreateInstanceRequest();
        request.setTenantId(tenantId);
        request.setRegion(dto.getRegion());
        request.setZone(dto.getZone());
        request.setImage(dto.getImage());
        request.setPodName(dto.getPodName());
        
        // GPU规格
        CreateInstanceRequest.GpuSpec gpuSpec = new CreateInstanceRequest.GpuSpec();
        gpuSpec.setModel(dto.getGpuSpec().getModel());
        gpuSpec.setCount(dto.getGpuSpec().getCount());
        request.setGpuSpec(gpuSpec);
        
        // 计费配置
        CreateInstanceRequest.Billing billing = new CreateInstanceRequest.Billing();
        billing.setMode(dto.getBilling().getMode());
        billing.setDuration(dto.getBilling().getDuration());
        request.setBilling(billing);
        
        // 资源配置
        if (dto.getResource() != null) {
            CreateInstanceRequest.Resource resource = new CreateInstanceRequest.Resource();
            resource.setCpu(dto.getResource().getCpu());
            resource.setMemory(dto.getResource().getMemory());
            resource.setSystemDisk(dto.getResource().getSystemDisk());
            resource.setDataDisk(dto.getResource().getDataDisk());
            request.setResource(resource);
        }
        
        // 调用外部服务创建实例
        InstanceResponse response = gpuPodApiClient.createInstance(request);
        
        // 保存实例信息到数据库
        SysCustomerGpuPodEntity entity = new SysCustomerGpuPodEntity();
        entity.setCustomerId(customerId);
        entity.setInstanceId(response.getInstanceId());
        entity.setInstanceName(response.getInstanceName());
        entity.setStatus(response.getStatus());
        entity.setRegionCode(dto.getRegion());
        entity.setZoneCode(dto.getZone());
        entity.setGpuModel(dto.getGpuSpec().getModel());
        entity.setGpuCount(dto.getGpuSpec().getCount());
        entity.setImageUrl(dto.getImage());
        entity.setBillingMode(dto.getBilling().getMode());
        
        // 从响应中提取其他信息
        if (response.getGpuInfo() != null) {
            entity.setGpuMemory(response.getGpuInfo().getMemory());
        }
        
        if (response.getBillingInfo() != null) {
            entity.setPricePerHour(response.getBillingInfo().getUnitPrice());
        }
        
        // 保存到数据库
        gpuPodMapper.insert(entity);
        
        // 返回VO
        return convertToVO(entity);
    }

    @Override
    public GpuPodInstanceVO getInstance(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        
        SysCustomerGpuPodEntity entity = gpuPodMapper.selectOne(
            Wrappers.<SysCustomerGpuPodEntity>lambdaQuery()
                .eq(SysCustomerGpuPodEntity::getId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, customerId)
                .eq(SysCustomerGpuPodEntity::getDelFlag, 0)
        );
        
        if (entity == null) {
            throw new ServiceException("实例不存在");
        }
        
        // 可选：从外部服务获取最新状态
        try {
            String tenantId = "tenant_" + customerId;
            InstanceResponse response = gpuPodApiClient.getInstance(entity.getInstanceId(), tenantId);
            // 更新本地状态
            if (response != null && response.getStatus() != null) {
                entity.setStatus(response.getStatus());
                gpuPodMapper.updateById(entity);
            }
        } catch (Exception e) {
            log.warn("获取实例最新状态失败: {}", e.getMessage());
        }
        
        return convertToVO(entity);
    }

    @Override
    public void startInstance(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务启动实例
        gpuPodApiClient.startInstance(entity.getInstanceId(), tenantId);
        
        // 更新本地状态
        entity.setStatus("starting");
        entity.setStartTime(LocalDateTime.now());
        gpuPodMapper.updateById(entity);
    }

    @Override
    public void stopInstance(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务停止实例
        gpuPodApiClient.stopInstance(entity.getInstanceId(), tenantId);
        
        // 更新本地状态
        entity.setStatus("stopping");
        entity.setStopTime(LocalDateTime.now());
        gpuPodMapper.updateById(entity);
    }

    @Override
    public void restartInstance(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务重启实例
        gpuPodApiClient.restartInstance(entity.getInstanceId(), tenantId);
        
        // 更新本地状态
        entity.setStatus("restarting");
        gpuPodMapper.updateById(entity);
    }

    @Override
    public void releaseInstance(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务释放实例
        gpuPodApiClient.releaseInstance(entity.getInstanceId(), tenantId);
        
        // 更新本地状态（软删除）
        entity.setStatus("releasing");
        entity.setReleaseTime(LocalDateTime.now());
        gpuPodMapper.updateById(entity);
    }

    @Override
    public String resetPassword(String id) {
        // TODO: 实现重置密码逻辑，可能需要调用外部服务
        return "NewPass@123";
    }

    @Override
    public void setInstanceName(String id, SetNameDTO dto) {
        Long customerId = SecurityUtils.getCustomerId();
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        entity.setInstanceName(dto.getName());
        gpuPodMapper.updateById(entity);
    }

    @Override
    public void setShutdownSchedule(String id, SetShutdownScheduleDTO dto) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务设置定时关机
        ShutdownScheduleRequest request = new ShutdownScheduleRequest();
        request.setShutdownTime(dto.getShutdownTime());
        gpuPodApiClient.setShutdownSchedule(entity.getInstanceId(), tenantId, request);
        
        // 更新本地记录
        if (dto.getShutdownTime() != null) {
            entity.setScheduledShutdownTime(LocalDateTime.parse(dto.getShutdownTime()));
        } else {
            entity.setScheduledShutdownTime(null);
        }
        gpuPodMapper.updateById(entity);
    }

    @Override
    public ShutdownScheduleVO getShutdownSchedule(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 从外部服务获取
        ShutdownScheduleResponse response = gpuPodApiClient.getShutdownSchedule(
            entity.getInstanceId(), tenantId);
        
        ShutdownScheduleVO vo = new ShutdownScheduleVO();
        if (response != null) {
            vo.setHasSchedule(response.isHasSchedule());
            vo.setShutdownTime(response.getShutdownTime());
            vo.setRemainingTime(response.getRemainingTime());
        } else {
            vo.setHasSchedule(false);
        }
        
        return vo;
    }

    @Override
    public SshInfoVO getSshInfo(String id) {
        Long customerId = SecurityUtils.getCustomerId();
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 从本地数据库获取
        SshInfoVO vo = new SshInfoVO();
        vo.setHost(entity.getSshHost());
        vo.setPort(entity.getSshPort() != null ? entity.getSshPort() : 22);
        vo.setUsername(entity.getSshUsername());
        vo.setPassword(entity.getSshPassword());  // 需要解密
        
        if (vo.getHost() != null && vo.getUsername() != null) {
            vo.setCommand(String.format("ssh -p %d %s@%s", vo.getPort(), vo.getUsername(), vo.getHost()));
        }
        
        return vo;
    }

    @Override
    public String getInstanceLogs(String id, int tailLines) {
        Long customerId = SecurityUtils.getCustomerId();
        String tenantId = "tenant_" + customerId;
        
        SysCustomerGpuPodEntity entity = getAndValidateInstance(id, customerId);
        
        // 调用外部服务获取日志
        return gpuPodApiClient.getInstanceLogs(entity.getInstanceId(), tenantId, tailLines);
    }

    // 私有辅助方法

    private SysCustomerGpuPodEntity getAndValidateInstance(String id, Long customerId) {
        SysCustomerGpuPodEntity entity = gpuPodMapper.selectOne(
            Wrappers.<SysCustomerGpuPodEntity>lambdaQuery()
                .eq(SysCustomerGpuPodEntity::getId, id)
                .eq(SysCustomerGpuPodEntity::getCustomerId, customerId)
                .eq(SysCustomerGpuPodEntity::getDelFlag, 0)
        );
        
        if (entity == null) {
            throw new ServiceException("实例不存在");
        }
        
        return entity;
    }

    private GpuPodInstanceVO convertToVO(SysCustomerGpuPodEntity entity) {
        GpuPodInstanceVO vo = new GpuPodInstanceVO();
        BeanUtils.copyProperties(entity, vo);
        
        vo.setId(String.valueOf(entity.getId()));
        vo.setName(entity.getInstanceName());
        vo.setRegion(entity.getRegionCode());
        vo.setRegionName(entity.getRegionName());
        vo.setZone(entity.getZoneCode());
        vo.setZoneName(entity.getZoneName());
        vo.setGpuType(entity.getGpuModel());
        vo.setCpuCores(entity.getCpuCores());
        vo.setMemory(entity.getMemorySize());
        vo.setSystemDisk(entity.getSystemDiskSize());
        vo.setDataDisk(entity.getDataDiskSize());
        vo.setBillingType(entity.getBillingMode());
        
        // 状态文本转换
        vo.setStatusText(convertStatusText(entity.getStatus()));
        
        // 计费类型文本
        vo.setBillingTypeText(convertBillingTypeText(entity.getBillingMode()));
        
        // SSH信息
        if (entity.getSshHost() != null) {
            SshInfoVO sshInfo = new SshInfoVO();
            sshInfo.setHost(entity.getSshHost());
            sshInfo.setPort(entity.getSshPort() != null ? entity.getSshPort() : 22);
            sshInfo.setUsername(entity.getSshUsername());
            sshInfo.setPassword(entity.getSshPassword());  // 需要解密
            sshInfo.setCommand(String.format("ssh -p %d %s@%s", 
                sshInfo.getPort(), sshInfo.getUsername(), sshInfo.getHost()));
            vo.setSshInfo(sshInfo);
        }
        
        return vo;
    }

    private String convertStatusText(String status) {
        if (status == null) return "未知";
        return switch (status) {
            case "running" -> "运行中";
            case "stopped" -> "已停止";
            case "starting" -> "启动中";
            case "stopping" -> "停止中";
            case "creating" -> "创建中";
            case "error" -> "异常";
            case "releasing" -> "释放中";
            case "released" -> "已释放";
            default -> "未知";
        };
    }

    private String convertBillingTypeText(String billingType) {
        if (billingType == null) return "未知";
        return switch (billingType) {
            case "on_demand" -> "按量计费";
            case "hourly" -> "按小时";
            case "daily" -> "包日";
            case "weekly" -> "包周";
            case "monthly" -> "包月";
            case "yearly" -> "包年";
            default -> "未知";
        };
    }
}
```

---

## Task 5: 创建 Controller

**Files:**
- Create: `ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/GpuPodController.java`

- [ ] **Step 1: 创建 Controller**

```java
package com.lingyang.cloud.api.controller.pc;

import com.lingyang.cloud.model.dto.CreateGpuPodDTO;
import com.lingyang.cloud.model.dto.SetNameDTO;
import com.lingyang.cloud.model.dto.SetShutdownScheduleDTO;
import com.lingyang.cloud.model.query.GpuPodInstanceQuery;
import com.lingyang.cloud.model.vo.GpuPodInstanceVO;
import com.lingyang.cloud.model.vo.ShutdownScheduleVO;
import com.lingyang.cloud.model.vo.SshInfoVO;
import com.lingyang.cloud.service.GpuPodService;
import com.lingyang.common.core.page.PageResult;
import com.lingyang.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * GPU Pod 实例管理控制器
 */
@RestController
@RequestMapping("/pc/gpu-pod")
@Tag(name = "PC端-GPU Pod实例管理", description = "GPU Pod实例生命周期管理相关接口")
public class GpuPodController {

    @Autowired
    private GpuPodService gpuPodService;

    // ==================== 实例列表与详情 ====================

    @GetMapping("/instances")
    @Operation(summary = "获取实例列表", description = "分页查询当前用户的GPU Pod实例列表")
    public Result<PageResult<GpuPodInstanceVO>> list(GpuPodInstanceQuery query) {
        return Result.success(gpuPodService.listInstances(query));
    }

    @PostMapping("/instances")
    @Operation(summary = "创建实例", description = "创建新的GPU Pod实例")
    public Result<GpuPodInstanceVO> create(@Valid @RequestBody CreateGpuPodDTO dto) {
        return Result.success(gpuPodService.createInstance(dto));
    }

    @GetMapping("/instances/{id}")
    @Operation(summary = "获取实例详情", description = "根据实例ID获取详细信息")
    public Result<GpuPodInstanceVO> detail(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        return Result.success(gpuPodService.getInstance(id));
    }

    // ==================== 实例生命周期操作 ====================

    @PostMapping("/instances/{id}/start")
    @Operation(summary = "启动实例", description = "启动已停止的GPU Pod实例")
    public Result<Void> start(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        gpuPodService.startInstance(id);
        return Result.success();
    }

    @PostMapping("/instances/{id}/stop")
    @Operation(summary = "停止实例", description = "停止运行中的GPU Pod实例")
    public Result<Void> stop(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        gpuPodService.stopInstance(id);
        return Result.success();
    }

    @PostMapping("/instances/{id}/restart")
    @Operation(summary = "重启实例", description = "重启GPU Pod实例")
    public Result<Void> restart(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        gpuPodService.restartInstance(id);
        return Result.success();
    }

    @PostMapping("/instances/{id}/release")
    @Operation(summary = "释放实例", description = "释放（删除）GPU Pod实例，数据不可恢复")
    public Result<Void> release(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        gpuPodService.releaseInstance(id);
        return Result.success();
    }

    // ==================== SSH 访问 ====================

    @GetMapping("/instances/{id}/ssh")
    @Operation(summary = "获取SSH信息", description = "获取GPU Pod实例的SSH连接信息")
    public Result<SshInfoVO> getSshInfo(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        return Result.success(gpuPodService.getSshInfo(id));
    }

    @PostMapping("/instances/{id}/reset-password")
    @Operation(summary = "重置SSH密码", description = "重置GPU Pod实例的SSH登录密码")
    public Result<String> resetPassword(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        return Result.success(gpuPodService.resetPassword(id));
    }

    // ==================== 实例名称 ====================

    @PutMapping("/instances/{id}/name")
    @Operation(summary = "设置实例名称", description = "修改GPU Pod实例的显示名称")
    public Result<Void> setName(
            @Parameter(description = "实例ID", required = true) @PathVariable String id,
            @Valid @RequestBody SetNameDTO dto) {
        gpuPodService.setInstanceName(id, dto);
        return Result.success();
    }

    // ==================== 定时关机 ====================

    @PostMapping("/instances/{id}/shutdown-schedule")
    @Operation(summary = "设置定时关机", description = "设置或取消GPU Pod实例的定时关机任务")
    public Result<Void> setShutdownSchedule(
            @Parameter(description = "实例ID", required = true) @PathVariable String id,
            @RequestBody SetShutdownScheduleDTO dto) {
        gpuPodService.setShutdownSchedule(id, dto);
        return Result.success();
    }

    @GetMapping("/instances/{id}/shutdown-schedule")
    @Operation(summary = "获取定时关机状态", description = "查询GPU Pod实例的定时关机任务状态")
    public Result<ShutdownScheduleVO> getShutdownSchedule(
            @Parameter(description = "实例ID", required = true) @PathVariable String id) {
        return Result.success(gpuPodService.getShutdownSchedule(id));
    }

    // ==================== Pod 日志 ====================

    @GetMapping("/instances/{id}/logs")
    @Operation(summary = "获取Pod日志", description = "获取GPU Pod实例的容器日志")
    public Result<String> getLogs(
            @Parameter(description = "实例ID", required = true) @PathVariable String id,
            @Parameter(description = "返回最后N行日志", example = "100") 
            @RequestParam(defaultValue = "100") int tailLines) {
        return Result.success(gpuPodService.getInstanceLogs(id, tailLines));
    }
}
```

---

## Task 6: 配置文件更新

**Files:**
- Modify: `ai-cloud-system/src/main/resources/application-dev.yml`

- [ ] **Step 1: 添加 GPU Pod 配置**

在 `application-dev.yml` 中添加：

```yaml
# GPU Pod 服务配置
gpu-pod:
  base-url: http://124.174.46.14:8000
  api-version: v1
  connect-timeout: 10000
  read-timeout: 30000
  # JWT Token 会在运行时从认证服务获取
```

---

## 测试与验证

### 单元测试

为每个 Service 方法编写单元测试：

1. `GpuPodServiceTest` - 测试所有实例生命周期操作
2. `GpuPodApiClientTest` - 测试外部 API 调用

### 集成测试

1. 启动应用，验证所有接口可访问
2. 使用 Postman 或类似工具测试每个 API 端点
3. 验证与外部 GPU Pod 服务的集成

### 手动验证清单

- [ ] 创建实例接口正常工作
- [ ] 实例列表查询分页正常
- [ ] 实例详情查询正常
- [ ] 启动/停止/重启/释放实例正常
- [ ] SSH 信息获取正常
- [ ] 定时关机设置/查询/取消正常
- [ ] Pod 日志获取正常
- [ ] 权限控制正确（只能操作自己的实例）

---

## 风险与注意事项

1. **外部服务可用性**：GPU Pod 服务是外部依赖，需要实现熔断降级机制
2. **数据一致性**：本地数据库与外部服务状态可能不一致，需要定期同步
3. **安全性**：SSH 密码需要加密存储和传输
4. **性能**：列表查询可能需要从外部服务获取实时状态，注意缓存策略
5. **错误处理**：外部服务返回的错误需要转换为友好的错误信息

---

## 后续优化方向

1. 实现实例状态定时同步任务
2. 添加缓存机制减少外部服务调用
3. 实现 WebSocket 实时推送实例状态变更
4. 添加更多监控指标和告警
5. 支持批量操作（批量启动/停止/释放）

---

**End of Implementation Plan**
