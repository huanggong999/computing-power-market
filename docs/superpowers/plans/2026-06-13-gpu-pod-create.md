# GPU Pod Create Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make the `computeRent` “创建并开机” button call the backend, and make the backend call the external GPU Pod `/api/v1/gpu/pod/create` API with frontend-provided Pod creation parameters.

**Architecture:** The frontend assembles the external API request payload from `gpuResource`, selected mirror version, selected billing type, selected quantity, and calculated fee. The backend receives this payload inside `GpuRentOrderDTO`, injects configured tenant information, calls `GpuPodApiClient.createGpuPod`, and returns the external Pod result as the order/instance result. This keeps mirror data static in the frontend while using the existing backend token/client infrastructure.

**Tech Stack:** Vue 3 + TypeScript (`ux-user-main`), Spring Boot 3 + Java 17 (`ai-cloud-system`), `RestTemplate`, existing `GpuPodApiClient`, existing `GpuPodProperties`.

---

## File Structure

- Modify: `ux-user-main/src/api/computeRent.ts`
  - Add `image` to `MirrorVersion` and static mirror version objects.
  - Add `PodCreateRequest` types and `podCreateRequest` to `CreateOrderParams`.
- Modify: `ux-user-main/src/view/computeRent/index.vue`
  - Build `podCreateRequest` before `createRentOrder(params)`.
  - Use selected mirror version `image`.
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/GpuPodCreateRequest.java`
  - DTO matching `docs/API.html` `/api/v1/gpu/pod/create` request shape.
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/GpuPodApiClient.java`
  - Add `createGpuPod(GpuPodCreateRequest request)` calling `/api/v1/gpu/pod/create`.
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/model/dto/GpuRentOrderDTO.java`
  - Add `GpuPodCreateRequest podCreateRequest`.
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/PcGpuRentController.java`
  - Inject `GpuPodApiClient` and `GpuPodProperties`.
  - Replace simulated order creation with real external Pod creation.

---

### Task 1: Add Frontend Pod Create Payload Types and Static Image URLs

**Files:**
- Modify: `ux-user-main/src/api/computeRent.ts`

- [ ] **Step 1: Extend `MirrorVersion`**

Add the `image` field to `MirrorVersion`:

```ts
export interface MirrorVersion {
  id: string
  mirrorId: string
  version: string
  cudaVersion: string
  pythonVersion: string
  description: string
  image: string
  frameworks?: {
    pytorch?: string
    tensorflow?: string
    [key: string]: string | undefined
  }
}
```

- [ ] **Step 2: Add image URLs to static versions**

Use the API document example for PyTorch 2.1 and deterministic static placeholders for the other frontend-static versions:

```ts
{
  id: 'pytorch-2.1',
  mirrorId: 'pytorch',
  version: 'PyTorch 2.1 / CUDA 12.1 / Python 3.10',
  cudaVersion: '12.1',
  pythonVersion: '3.10',
  description: 'PyTorch 2.1 / CUDA 12.1 / Python 3.10',
  image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch:2.5.0-cuda12.4-cudnn9-devel',
  frameworks: { pytorch: '2.1' }
}
```

For other versions use:

```ts
image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch:2.0-cuda11.8-python3.10'
image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorflow:2.15-cuda12.1-python3.10'
image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorflow:2.12-cuda11.8-python3.10'
image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda:py310'
image: 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda:py311'
```

- [ ] **Step 3: Add Pod create request types**

Add below `CreateOrderParams` related interfaces:

```ts
export interface PodCreateRequest {
  gpuSpec: {
    model: string
    count: number
    gpuMemory: string
  }
  image: string
  billing: {
    mode: string
    duration: number
  }
  resource: {
    cpu: string
    cpuModel: string
    memory: string
    disk: string
    systemDisk: string
    dataDisk: string
    dataDiskExpandable: string
  }
  pricing: {
    unitPrice: number
    discountUnitPrice: number
    totalCost: number
    discountTotalCost: number
    currency: string
    unit: string
    pricePerHour?: number
    discountPrice?: number
  }
  region: string
  zone: string
  machineId: string
  gpuDriver: string
  cudaVersion: string
  podName?: string
}
```

Update `CreateOrderParams`:

```ts
export interface CreateOrderParams {
  resourceId: number
  mirrorId: string
  mirrorVersionId: string
  billingType: 'hourly' | 'daily' | 'weekly' | 'monthly'
  quantity: number
  duration?: number
  couponId?: string
  agreeProtocol: boolean
  podCreateRequest: PodCreateRequest
}
```

- [ ] **Step 4: Verify frontend build**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: build completes with `✓ built`.

---

### Task 2: Build Pod Create Payload in computeRent

**Files:**
- Modify: `ux-user-main/src/view/computeRent/index.vue`

- [ ] **Step 1: Import `PodCreateRequest`**

Change the type import block to include `type PodCreateRequest`.

- [ ] **Step 2: Add billing mapping helpers**

Near `billingTypeNameMap` / `billingUnitMap`, add:

```ts
const billingModeMap: Record<string, string> = {
  hourly: 'on_demand',
  daily: 'daily',
  weekly: 'weekly',
  monthly: 'monthly'
}

const billingUnitTextMap: Record<string, string> = {
  hourly: '小时',
  daily: '天',
  weekly: '周',
  monthly: '月'
}
```

- [ ] **Step 3: Add selected mirror version computed**

Below `filteredMirrors`, add:

```ts
const selectedMirrorVersion = computed(() => {
  if (!rentConfig.versionId) return null
  return versionList.value.find(version => version.id === rentConfig.versionId) || null
})
```

- [ ] **Step 4: Add `buildPodCreateRequest` function**

Add before `handleSubmit`:

```ts
function buildPodCreateRequest(): PodCreateRequest | null {
  const resource = gpuResource.value
  const price = currentPriceItem.value
  const version = selectedMirrorVersion.value
  const fee = feeResult.value
  if (!resource || !price || !version || !fee) return null

  const unitPrice = Number(price.unitPrice || 0)
  const discountUnitPrice = Number(price.discountPrice || price.unitPrice || 0)

  return {
    gpuSpec: {
      model: resource.model || '',
      count: rentConfig.quantity,
      gpuMemory: resource.vram || ''
    },
    image: version.image,
    billing: {
      mode: billingModeMap[rentConfig.billingType] || rentConfig.billingType,
      duration: 1
    },
    resource: {
      cpu: String(instanceCpu.value || resource.cpuCores || ''),
      cpuModel: resource.cpuModel || '',
      memory: instanceMemory.value || resource.memory || '',
      disk: resource.dataDisk || '',
      systemDisk: resource.systemDisk || '',
      dataDisk: resource.dataDisk || '',
      dataDiskExpandable: resource.expandable || '0 GB'
    },
    pricing: {
      unitPrice,
      discountUnitPrice,
      totalCost: fee.subtotal,
      discountTotalCost: fee.total,
      currency: 'CNY',
      unit: billingUnitTextMap[rentConfig.billingType] || rentConfig.billingType,
      pricePerHour: unitPrice,
      discountPrice: discountUnitPrice
    },
    region: resource.regionCode || '',
    zone: resource.zoneCode || '',
    machineId: resource.machineId || '',
    gpuDriver: resource.gpuDriver || '',
    cudaVersion: resource.cudaVersion || ''
  }
}
```

- [ ] **Step 5: Include `podCreateRequest` in submit params**

In `handleSubmit`, before `const params = { ... }`, add:

```ts
const podCreateRequest = buildPodCreateRequest()
if (!podCreateRequest) {
  ElMessage.error('创建参数不完整，请重新选择配置')
  submitting.value = false
  return
}
```

Add to `params`:

```ts
podCreateRequest
```

- [ ] **Step 6: Verify frontend build**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: build completes with `✓ built`.

---

### Task 3: Add Backend GPU Pod Create Request DTO

**Files:**
- Create: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/dto/GpuPodCreateRequest.java`

- [ ] **Step 1: Create DTO**

```java
package com.lingyang.cloud.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建 GPU Pod 请求（对接外部 GPU 调度服务 /api/v1/gpu/pod/create）
 */
@Data
public class GpuPodCreateRequest {

    private String tenantId;
    private String tenantName;
    private GpuSpec gpuSpec;
    private String image;
    private Billing billing;
    private Resource resource;
    private Pricing pricing;
    private String podName;
    private String region;
    private String zone;
    private String machineId;
    private String gpuDriver;
    private String cudaVersion;

    @Data
    public static class GpuSpec {
        private String model;
        private Integer count;
        private String gpuMemory;
    }

    @Data
    public static class Billing {
        private String mode;
        private Integer duration;
    }

    @Data
    public static class Resource {
        private String cpu;
        private String cpuModel;
        private String memory;
        private String disk;
        private String systemDisk;
        private String dataDisk;
        private String dataDiskExpandable;
    }

    @Data
    public static class Pricing {
        private BigDecimal unitPrice;
        private BigDecimal discountUnitPrice;
        private BigDecimal totalCost;
        private BigDecimal discountTotalCost;
        private String currency;
        private String unit;
        private BigDecimal pricePerHour;
        private BigDecimal discountPrice;
    }

    @JsonProperty("tenant_id")
    public String getTenantIdSnake() {
        return tenantId;
    }

    @JsonProperty("tenant_name")
    public String getTenantNameSnake() {
        return tenantName;
    }

    @JsonProperty("pod_name")
    public String getPodNameSnake() {
        return podName;
    }

    @JsonProperty("machine_id")
    public String getMachineIdSnake() {
        return machineId;
    }

    @JsonProperty("gpu_driver")
    public String getGpuDriverSnake() {
        return gpuDriver;
    }

    @JsonProperty("cuda_version")
    public String getCudaVersionSnake() {
        return cudaVersion;
    }
}
```

- [ ] **Step 2: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

### Task 4: Add GpuPodApiClient createGpuPod Method

**Files:**
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/client/GpuPodApiClient.java`

- [ ] **Step 1: Import DTO**

Add imports:

```java
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import com.lingyang.cloud.client.dto.GpuPodCreateResponse;
```

- [ ] **Step 2: Add method**

Add after `createInstance`:

```java
/**
 * 创建 GPU Pod（对接外部 /api/v1/gpu/pod/create）
 */
public GpuPodCreateResponse createGpuPod(GpuPodCreateRequest request) {
    String url = buildApiUrl("/gpu/pod/create");
    try {
        log.info("Creating GPU Pod for tenant: {}, machine: {}", request.getTenantId(), request.getMachineId());
        HttpEntity<GpuPodCreateRequest> entity = new HttpEntity<>(request, createHeaders());
        ResponseEntity<GpuPodCreateResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, GpuPodCreateResponse.class);
        return response.getBody();
    } catch (RestClientResponseException e) {
        log.error("Failed to create GPU Pod: {}", e.getResponseBodyAsString(), e);
        GpuPodCreateResponse response = new GpuPodCreateResponse();
        response.setSuccess(false);
        response.setMessage("创建 GPU Pod 失败: " + e.getResponseBodyAsString());
        return response;
    } catch (Exception e) {
        log.error("Failed to create GPU Pod", e);
        GpuPodCreateResponse response = new GpuPodCreateResponse();
        response.setSuccess(false);
        response.setMessage("创建 GPU Pod 失败: " + e.getMessage());
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

### Task 5: Wire PcGpuRentController to External Pod Create

**Files:**
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/model/dto/GpuRentOrderDTO.java`
- Modify: `console-main/ai-cloud-system/src/main/java/com/lingyang/cloud/api/controller/pc/PcGpuRentController.java`

- [ ] **Step 1: Add podCreateRequest to DTO**

In `GpuRentOrderDTO.java` add:

```java
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
```

Then field:

```java
private GpuPodCreateRequest podCreateRequest;
```

- [ ] **Step 2: Inject client and properties**

In `PcGpuRentController.java`, add imports:

```java
import com.lingyang.cloud.client.GpuPodApiClient;
import com.lingyang.cloud.client.config.GpuPodProperties;
import com.lingyang.cloud.client.dto.GpuPodCreateRequest;
import com.lingyang.cloud.client.dto.GpuPodCreateResponse;
```

Add fields:

```java
@Resource
private GpuPodApiClient gpuPodApiClient;

@Resource
private GpuPodProperties gpuPodProperties;
```

- [ ] **Step 3: Replace simulated order creation**

Replace `order(@RequestBody GpuRentOrderDTO dto)` body with:

```java
@PostMapping("/rent/order")
@Operation(summary = "创建GPU租用订单")
public Result<GpuRentOrderVO> order(@RequestBody GpuRentOrderDTO dto) {
    Result<GpuRentFeeVO> feeResult = calculate(dto);
    if (feeResult.getData() == null) {
        return Result.error("创建订单失败");
    }

    GpuPodCreateRequest request = dto.getPodCreateRequest();
    if (request == null) {
        return Result.error("GPU Pod 创建参数不能为空");
    }
    request.setTenantId(gpuPodProperties.getTenantId());
    request.setTenantName(gpuPodProperties.getTenantName());

    GpuPodCreateResponse podResponse = gpuPodApiClient.createGpuPod(request);
    if (podResponse == null || !podResponse.isSuccess()) {
        return Result.error(podResponse == null ? "GPU Pod 创建失败" : podResponse.getMessage());
    }

    LocalDateTime now = LocalDateTime.now();
    GpuRentOrderVO vo = new GpuRentOrderVO();
    String podName = podResponse.getPodName();
    vo.setOrderId(podName);
    vo.setOrderNo(podName);
    vo.setStatus("processing");
    vo.setTotalAmount(feeResult.getData().getTotal());
    vo.setPaidAmount(feeResult.getData().getTotal());
    vo.setCreateTime(now);

    GpuRentOrderVO.InstanceInfo instanceInfo = new GpuRentOrderVO.InstanceInfo();
    instanceInfo.setInstanceId(podName);
    instanceInfo.setInstanceName(podName);
    instanceInfo.setStatus("creating");
    vo.setInstanceInfo(instanceInfo);
    return Result.success(vo);
}
```

- [ ] **Step 4: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

---

### Task 6: End-to-End Build Verification

**Files:**
- None

- [ ] **Step 1: Build frontend**

Run:

```bash
npm --prefix /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/ux-user-main run build
```

Expected: `✓ built`.

- [ ] **Step 2: Compile backend**

Run:

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) mvn -f /Users/liujiaxing/coding/computing-power-market/yiyunshuzi/console-main/ai-cloud-system/pom.xml compile -DskipTests
```

Expected: `BUILD SUCCESS`.

- [ ] **Step 3: Manual verification after restart**

Restart backend and frontend. Open:

```text
http://localhost:4173/#/computeRent?resourceId=1
```

Select a base mirror and version, then click `创建并开机`.

Expected network behavior:

1. Browser sends `POST /pc/gpu/rent/order` with `podCreateRequest`.
2. Backend logs `Creating GPU Pod for tenant...`.
3. Backend calls external `{gpu-pod.base-url}/api/v1/gpu/pod/create`.
4. UI shows `实例创建成功` and routes to `/Cloud/instance`.

---

## Self-Review

- Spec coverage: covers frontend param passthrough, static image fallback, backend DTO, external API client call, and controller wiring.
- Placeholder scan: no TBD/TODO placeholders remain; image values are concrete static strings.
- Type consistency: frontend `podCreateRequest` maps to backend `GpuPodCreateRequest`; `GpuRentOrderDTO.podCreateRequest` is the bridge; `GpuPodCreateResponse` already exists.
