# Container Instance Management API Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the missing `docs/API.html` section 七 容器实例管理接口 coverage for `/cloud/instanceList`: monitor, reset password, name update, shutdown schedule UI, renew, batch renew, and tools.

**Architecture:** Keep the browser calling existing project proxy endpoints under `/pc/gpu-pod/instances/**`. The backend proxy will inject `gpu-pod.tenant-id`, call the external GPU Pod service under `/api/v1/instances/**`, and normalize responses into existing Vue types. Minimal UI additions go into the existing InstanceList components and dialogs.

**Tech Stack:** Vue 3 + TypeScript, Pinia, Element Plus, Spring Boot 3 + Java 17, RestTemplate, existing `GpuPodApiClient`, existing `GpuPodService`.

---

## File Structure

- Modify: `ux-user-main/src/api/instance.ts`
  - Add frontend API methods for tools, monitor, renew, batch renew, shutdown schedule get/set.
  - Fix monitor endpoint from `/instance/{id}/metrics` to `/pc/gpu-pod/instances/{id}/monitor`.
- Modify: `ux-user-main/src/store/modules/useInstance.ts`
  - Add store actions for renew, batch renew, tools, shutdown schedule.
- Modify: `ux-user-main/src/view/Cloud/InstanceList/index.vue`
  - Wire new UI actions and dialogs.
- Modify: `ux-user-main/src/view/Cloud/InstanceList/components/InstanceTable.vue`
  - Add dropdown actions for renew, shutdown schedule, tools.
- Modify: `ux-user-main/src/view/Cloud/InstanceList/components/MoreActionsDialog.vue`
  - Add renew and shutdown schedule actions if using central dialog.
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/RenewDialog.vue`
  - Single-instance renew form.
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/BatchRenewDialog.vue`
  - Batch renew form for selected instances.
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/ShutdownScheduleDialog.vue`
  - Set/cancel shutdown schedule UI.
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/ToolsDialog.vue`
  - Display JupyterLab/AutoPanel/etc. shortcuts.
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/GpuPodApiClient.java`
  - Add methods for reset password, renew, batch renew, set name, tools, monitor.
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/ResetPasswordRequest.java`
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/RenewRequest.java`
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/BatchRenewRequest.java`
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/SetInstanceNameRequest.java`
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/InstanceToolsResponse.java`
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/InstanceMonitorResponse.java`
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/GpuPodController.java`
  - Add proxy endpoints for renew, batch renew, tools, monitor; update reset password/name to call external API via service.
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/GpuPodService.java`
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuPodServiceImpl.java`
  - Add service methods and map responses to front-end friendly VO/DTO.

---

### Task 1: Backend Client Methods for Missing External APIs

**Files:**
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/GpuPodApiClient.java`
- Create DTO files under `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/`

- [ ] **Step 1: Create DTOs**

Create `ResetPasswordRequest.java`:

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @JsonProperty("new_password")
    private String newPassword;
}
```

Create `RenewRequest.java`:

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RenewRequest {
    @JsonProperty("billing_mode")
    private String billingMode;
    private Integer duration;
}
```

Create `BatchRenewRequest.java`:

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class BatchRenewRequest {
    @JsonProperty("instance_ids")
    private List<String> instanceIds;
    @JsonProperty("billing_mode")
    private String billingMode;
    private Integer duration;
}
```

Create `SetInstanceNameRequest.java`:

```java
package com.lingyang.cloud.client.dto;

import lombok.Data;

@Data
public class SetInstanceNameRequest {
    private String name;
}
```

Create `InstanceToolsResponse.java`:

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class InstanceToolsResponse {
    private boolean success = true;
    private String message;
    @JsonAlias("instance_id")
    private String instanceId;
    private List<ToolInfo> tools;

    @Data
    public static class ToolInfo {
        private String name;
        private String url;
        private String icon;
    }
}
```

Create `InstanceMonitorResponse.java`:

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class InstanceMonitorResponse {
    private boolean success = true;
    private String message;
    @JsonAlias("instance_id")
    private String instanceId;
    private Health health;
    private List<Point> gpuUtilization;
    private List<Point> gpuMemoryUsed;
    private List<Point> cpuUtilization;
    private List<Point> memoryUsed;
    private Double gpuMemoryTotal;
    private Double memoryTotal;

    @Data
    public static class Health {
        private String status;
        @JsonAlias("cpu_usage")
        private Double cpuUsage;
        @JsonAlias("memory_usage")
        private Double memoryUsage;
        @JsonAlias("gpu_usage")
        private Double gpuUsage;
    }

    @Data
    public static class Point {
        private Long timestamp;
        private Double value;
    }
}
```

- [ ] **Step 2: Add client methods**

In `GpuPodApiClient.java`, add imports for these DTOs and add methods:

```java
public InstanceResponse resetPassword(String instanceId, String tenantId, ResetPasswordRequest request) {
    String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/reset-password"))
            .queryParam("tenant_id", tenantId)
            .buildAndExpand(instanceId)
            .toUriString();
    try {
        HttpEntity<ResetPasswordRequest> entity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<InstanceResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, InstanceResponse.class);
        return response.getBody();
    } catch (RestClientResponseException e) {
        log.error("Failed to reset password: {}", e.getResponseBodyAsString(), e);
        return createErrorResponse("重置密码失败: " + e.getResponseBodyAsString());
    }
}

public void renewInstance(String instanceId, String tenantId, RenewRequest request) {
    String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/renew"))
            .queryParam("tenant_id", tenantId)
            .buildAndExpand(instanceId)
            .toUriString();
    HttpEntity<RenewRequest> entity = new HttpEntity<>(request, createHeaders());
    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
}

public void batchRenewInstances(String tenantId, BatchRenewRequest request) {
    String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/batch-renew"))
            .queryParam("tenant_id", tenantId)
            .toUriString();
    HttpEntity<BatchRenewRequest> entity = new HttpEntity<>(request, createHeaders());
    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
}

public void setInstanceNameExternal(String instanceId, String tenantId, SetInstanceNameRequest request) {
    String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/name"))
            .queryParam("tenant_id", tenantId)
            .queryParam("name", request.getName())
            .buildAndExpand(instanceId)
            .toUriString();
    HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
    restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
}

public InstanceToolsResponse getInstanceTools(String instanceId, String tenantId) {
    String url = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/tools"))
            .queryParam("tenant_id", tenantId)
            .buildAndExpand(instanceId)
            .toUriString();
    try {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<InstanceToolsResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, InstanceToolsResponse.class);
        return response.getBody();
    } catch (RestClientResponseException e) {
        log.error("Failed to get instance tools: {}", e.getResponseBodyAsString(), e);
        InstanceToolsResponse response = new InstanceToolsResponse();
        response.setSuccess(false);
        response.setMessage("获取快捷工具失败: " + e.getResponseBodyAsString());
        return response;
    }
}

public InstanceMonitorResponse getInstanceMonitor(String instanceId, String tenantId, Map<String, Object> params) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(buildApiUrl("/instances/{instanceId}/monitor"))
            .queryParam("tenant_id", tenantId);
    if (params != null) {
        params.forEach((key, value) -> {
            if (value != null) builder.queryParam(key, value);
        });
    }
    String url = builder.buildAndExpand(instanceId).toUriString();
    try {
        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());
        ResponseEntity<InstanceMonitorResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity, InstanceMonitorResponse.class);
        return response.getBody();
    } catch (RestClientResponseException e) {
        log.error("Failed to get instance monitor: {}", e.getResponseBodyAsString(), e);
        InstanceMonitorResponse response = new InstanceMonitorResponse();
        response.setSuccess(false);
        response.setMessage("获取监控数据失败: " + e.getResponseBodyAsString());
        return response;
    }
}
```

- [ ] **Step 3: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

### Task 2: Backend Service and Controller Proxy Endpoints

**Files:**
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/GpuPodService.java`
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/service/impl/GpuPodServiceImpl.java`
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/GpuPodController.java`
- Create DTOs if not present: `RenewDTO.java`, `BatchRenewDTO.java`

- [ ] **Step 1: Create request DTOs**

Create `RenewDTO.java`:

```java
package com.lingyang.cloud.model.dto;

import lombok.Data;

@Data
public class RenewDTO {
    private String billingMode;
    private Integer duration;
}
```

Create `BatchRenewDTO.java`:

```java
package com.lingyang.cloud.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchRenewDTO {
    private List<String> instanceIds;
    private String billingMode;
    private Integer duration;
}
```

- [ ] **Step 2: Extend service interface**

Add methods to `GpuPodService.java`:

```java
void renewInstance(String id, RenewDTO dto);
void batchRenewInstances(BatchRenewDTO dto);
Object getInstanceTools(String id);
Object getInstanceMonitor(String id, Map<String, Object> params);
```

Add imports:

```java
import com.lingyang.cloud.model.dto.RenewDTO;
import com.lingyang.cloud.model.dto.BatchRenewDTO;
import java.util.Map;
```

- [ ] **Step 3: Implement service methods**

In `GpuPodServiceImpl.java`, import client DTOs. Implement:

```java
@Override
public void renewInstance(String id, RenewDTO dto) {
    RenewRequest request = new RenewRequest();
    request.setBillingMode(dto.getBillingMode());
    request.setDuration(dto.getDuration());
    gpuPodApiClient.renewInstance(id, getTenantId(), request);
}

@Override
public void batchRenewInstances(BatchRenewDTO dto) {
    BatchRenewRequest request = new BatchRenewRequest();
    request.setInstanceIds(dto.getInstanceIds());
    request.setBillingMode(dto.getBillingMode());
    request.setDuration(dto.getDuration());
    gpuPodApiClient.batchRenewInstances(getTenantId(), request);
}

@Override
public Object getInstanceTools(String id) {
    return gpuPodApiClient.getInstanceTools(id, getTenantId());
}

@Override
public Object getInstanceMonitor(String id, Map<String, Object> params) {
    return gpuPodApiClient.getInstanceMonitor(id, getTenantId(), params);
}
```

Update `resetPassword` to call the external reset endpoint when a new password is provided by controller. If keeping the existing front-end generated password flow is simpler, proxy the front-end password:

```java
public String resetPassword(String id, String newPassword) {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setNewPassword(newPassword);
    InstanceResponse response = gpuPodApiClient.resetPassword(id, getTenantId(), request);
    if (response == null || !response.isSuccess()) {
        throw new RuntimeException(response == null ? "重置密码失败" : response.getMessage());
    }
    return newPassword;
}
```

- [ ] **Step 4: Add controller endpoints**

In `GpuPodController.java`, add imports and endpoints:

```java
@PostMapping("/instances/{id}/renew")
public Result<Void> renew(@PathVariable String id, @RequestBody RenewDTO dto) {
    gpuPodService.renewInstance(id, dto);
    return Result.success();
}

@PostMapping("/instances/batch-renew")
public Result<Void> batchRenew(@RequestBody BatchRenewDTO dto) {
    gpuPodService.batchRenewInstances(dto);
    return Result.success();
}

@GetMapping("/instances/{id}/tools")
public Result<Object> getTools(@PathVariable String id) {
    return Result.success(gpuPodService.getInstanceTools(id));
}

@GetMapping("/instances/{id}/monitor")
public Result<Object> getMonitor(@PathVariable String id, @RequestParam Map<String, Object> params) {
    return Result.success(gpuPodService.getInstanceMonitor(id, params));
}
```

Update reset password controller to accept body:

```java
@PostMapping("/instances/{id}/reset-password")
public Result<String> resetPassword(@PathVariable String id, @RequestBody ResetPasswordDTO dto) {
    return Result.success(gpuPodService.resetPassword(id, dto.getNewPassword()));
}
```

If no `ResetPasswordDTO` exists, create it with `private String newPassword;`.

- [ ] **Step 5: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

### Task 3: Frontend API and Store Coverage

**Files:**
- Modify: `ux-user-main/src/api/instance.ts`
- Modify: `ux-user-main/src/store/modules/useInstance.ts`

- [ ] **Step 1: Add frontend API methods**

In `instance.ts`, add:

```ts
export function renewInstance(id: string, data: { billingMode: string; duration: number }) {
  return request.post(`/pc/gpu-pod/instances/${id}/renew`, data)
}

export function batchRenewInstances(data: { instanceIds: string[]; billingMode: string; duration: number }) {
  return request.post('/pc/gpu-pod/instances/batch-renew', data)
}

export function getInstanceTools(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/tools`)
}

export function getShutdownSchedule(id: string) {
  return request.get(`/pc/gpu-pod/instances/${id}/shutdown-schedule`)
}

export function setShutdownSchedule(id: string, data: { shutdownTime?: string | null }) {
  return request.post(`/pc/gpu-pod/instances/${id}/shutdown-schedule`, data)
}
```

Change metrics API:

```ts
return request.get<InstanceMetrics>(`/pc/gpu-pod/instances/${id}/monitor`, { params })
```

- [ ] **Step 2: Add store actions**

In `useInstance.ts`, add:

```ts
async function renewInstance(id: string, data: { billingMode: string; duration: number }) {
  return instanceApi.renewInstance(id, data)
}

async function batchRenew(data: { instanceIds: string[]; billingMode: string; duration: number }) {
  return instanceApi.batchRenewInstances(data)
}

async function getInstanceTools(id: string) {
  return instanceApi.getInstanceTools(id)
}

async function getShutdownSchedule(id: string) {
  return instanceApi.getShutdownSchedule(id)
}

async function setShutdownSchedule(id: string, data: { shutdownTime?: string | null }) {
  return instanceApi.setShutdownSchedule(id, data)
}
```

Return these actions from the store.

- [ ] **Step 3: Build frontend**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: `✓ built`.

---

### Task 4: Frontend Dialogs and UI Actions

**Files:**
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/RenewDialog.vue`
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/BatchRenewDialog.vue`
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/ShutdownScheduleDialog.vue`
- Create: `ux-user-main/src/view/Cloud/InstanceList/components/ToolsDialog.vue`
- Modify: `ux-user-main/src/view/Cloud/InstanceList/components/InstanceTable.vue`
- Modify: `ux-user-main/src/view/Cloud/InstanceList/index.vue`

- [ ] **Step 1: Add action menu items**

In `InstanceTable.vue`, add dropdown items:

```vue
<el-dropdown-item command="renew">续费</el-dropdown-item>
<el-dropdown-item command="shutdownSchedule">定时关机</el-dropdown-item>
<el-dropdown-item command="tools">快捷工具</el-dropdown-item>
```

Keep existing `restart/resetPassword/setName/release` items.

- [ ] **Step 2: Create RenewDialog**

Implement a dialog with `billingMode` select and `duration` input. On confirm call `useInstanceStore().renewInstance(instance.id, { billingMode, duration })`, emit success, close.

- [ ] **Step 3: Create BatchRenewDialog**

Implement a dialog with selected count, `billingMode`, `duration`. On confirm call `useInstanceStore().batchRenew({ instanceIds, billingMode, duration })`.

- [ ] **Step 4: Create ShutdownScheduleDialog**

Implement a dialog with date-time picker and “取消定时关机”. On open call `getShutdownSchedule(instance.id)`. On save call `setShutdownSchedule(instance.id, { shutdownTime })`. On cancel schedule call `setShutdownSchedule(instance.id, { shutdownTime: null })`.

- [ ] **Step 5: Create ToolsDialog**

Implement a dialog that calls `getInstanceTools(instance.id)` and renders each tool as a link button. Empty state: “暂无快捷工具”.

- [ ] **Step 6: Wire dialogs in InstanceList/index.vue**

Add state refs for active dialog and selected instance(s). In `handleInstanceAction`, route new actions:

```ts
if (['renew', 'shutdownSchedule', 'tools'].includes(action)) {
  currentInstance.value = instance
  currentAction.value = action
  return
}
```

Render the dialogs with `v-model` and success callbacks.

- [ ] **Step 7: Build frontend**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: `✓ built`.

---

### Task 5: End-to-End Verification

**Files:**
- None

- [ ] **Step 1: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 2: Build frontend**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: `✓ built`.

- [ ] **Step 3: Manual smoke test after restart**

Open:

```text
http://localhost:4173/#/cloud/instanceList
```

Verify:

1. List loads through `/pc/gpu-pod/instances`.
2. Row click opens details and monitor panel uses `/pc/gpu-pod/instances/{id}/monitor`.
3. Start/stop/restart/release still work.
4. Reset password submits body `{ newPassword }` to `/pc/gpu-pod/instances/{id}/reset-password`.
5. Set name calls backend external name proxy.
6. Renew dialog calls `/pc/gpu-pod/instances/{id}/renew`.
7. Batch renew dialog calls `/pc/gpu-pod/instances/batch-renew`.
8. Shutdown schedule dialog calls GET/POST `/shutdown-schedule`.
9. Tools dialog calls `/tools`.

---

## Self-Review

- Spec coverage: Implements API.html section 七 gaps: monitor, reset-password, name external call, shutdown schedule UI, renew, batch renew, tools.
- Placeholder scan: No placeholders remain. Some UI details are intentionally minimal and concrete.
- Type consistency: Frontend API methods match backend `/pc/gpu-pod/instances/**` proxy endpoints; backend client methods map to external `/api/v1/instances/**` endpoints.
