# GPU Pod SSH 访问管理

## 概述

GPU Pod SSH 访问管理功能允许用户查看 GPU Pod 实例的 SSH 连接信息，包括主机地址、端口、用户名和密码，以便通过 SSH 客户端远程连接到实例。

## 功能需求

## 新增需求

### 需求:
用户可以查看指定 GPU Pod 实例的 SSH 连接信息。

#### 场景:
作为实例所有者，我想要查看我的 GPU Pod 实例的 SSH 连接信息，以便通过 SSH 客户端远程连接到实例。

**输入**：
- instanceId: 实例ID（路径参数）

**输出**：
- SSH 连接信息：
  - host: 主机地址
  - port: 端口号
  - username: 用户名
  - password: 密码（明文）
  - command: SSH 连接命令示例

**业务规则**：
- 只能查询自己创建的实例的 SSH 信息
- 实例状态必须为 running 才能获取有效的 SSH 信息
- 密码明文展示，但前端需要做安全处理（如一键复制、遮罩显示等）

## 接口规范

### 后端接口

```java
@GetMapping("/instances/{id}/ssh")
@Operation(summary = "获取SSH信息")
public Result<SshInfoVO> getSshInfo(@PathVariable String id);
```

### 前端 API

```typescript
// 获取SSH信息
export function getInstanceSshInfo(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/ssh`)
}
```

### 请求/响应示例

**请求**：
```
GET /pc/gpu-pod/instances/a1b2c3d4e5f6/ssh
```

**响应**：
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "host": "fa464e9524.xibei-b.autodl.com",
    "port": 22,
    "username": "root",
    "password": "aBcD1234EfGh",
    "command": "ssh -p 22 root@fa464e9524.xibei-b.autodl.com"
  }
}
```

## 前端交互设计

### SSH 信息展示对话框

- **触发方式**：在"更多操作"菜单中点击"查看SSH信息"
- **展示内容**：
  - 主机地址（可复制）
  - 端口号
  - 用户名
  - 密码（默认遮罩显示，可点击显示/隐藏）
  - SSH连接命令（一键复制按钮）
- **安全提示**：显示"请勿将密码分享给他人"等安全提示

### 使用流程

1. 用户在实例列表中找到目标实例
2. 点击"更多"按钮，选择"查看SSH信息"
3. 弹出SSH信息对话框，展示连接信息
4. 用户可以：
   - 复制主机地址、用户名、密码
   - 一键复制完整的SSH连接命令
   - 点击"使用WebSSH连接"（如果集成了WebSSH功能）

## 安全考虑

1. **权限控制**：只有实例的创建者才能查看SSH信息
2. **密码保护**：前端需要实现密码遮罩功能，默认不显示明文密码
3. **传输安全**：后端与GPU Pod服务之间使用HTTPS通信
4. **审计日志**：记录SSH信息查询操作，用于安全审计
5. **密码重置**：提供重置密码功能，以便在密码泄露时快速响应

## 错误处理

| 错误场景 | 错误码 | 处理建议 |
|---------|--------|---------|
| 实例不存在 | 404 | 检查实例ID是否正确 |
| 无权访问 | 403 | 只能查看自己的实例 |
| 实例未运行 | 409 | 实例必须为running状态才能查看SSH |
| GPU Pod服务不可用 | 503 | 检查GPU Pod服务状态 |

## 依赖关系

本功能依赖于以下功能：
- `gpu-pod-instance-management`: 需要先完成实例管理功能，才能获取实例ID查询SSH信息
