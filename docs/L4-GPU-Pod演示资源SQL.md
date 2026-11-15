# L4 GPU Pod 演示资源 SQL

本文档用于直接向后端数据库插入一条可用于客户演示的 L4 GPU 资源。

该资源参数与外部 GPU Pod 创建接口 `/api/v1/gpu/pod/create` 当前可用参数保持一致：

- GPU 型号：`L4`
- 显存：`24 GB`
- 地区：`beijing-b`
- 专区：`B01`
- 机器 ID：`l4-002`
- GPU 驱动：`550.54.15`
- CUDA：`<= 12.4`
- CPU：`4`
- 内存：`16Gi`
- 系统盘：`30Gi`
- 数据盘：`50Gi`
- 可扩容：`100Gi`
- 计费方式：`hourly`
- 原价：`2.50`
- 折扣价：`1.88`

执行后使用最后一条查询返回的 `resource_id` 打开：

```text
http://localhost:4173/#/computeRent?resourceId=<resource_id>
```

选择：

```text
基础镜像 -> PyTorch 2.1 / CUDA 12.1 / Python 3.10
```

该镜像已在前端静态配置中改为外部接口验证可用的 runtime 镜像：

```text
acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch:2.5.0-cuda12.4-cudnn9-runtime
```

---

## SQL

```sql
-- ============================================================
-- L4 GPU Pod 演示资源
-- 用于对接外部 /api/v1/gpu/pod/create 可成功创建的参数
-- ============================================================

-- 1. GPU 规格：L4 / 24 GB
INSERT INTO gpu_resource_spec (
    model,
    vram,
    architecture,
    description,
    tags,
    sort_order,
    status,
    del_flag
) VALUES (
    'L4',
    '24 GB',
    'Ada Lovelace',
    'NVIDIA L4 演示 GPU，适用于客户演示 GPU Pod 创建',
    '["演示","GPU Pod","L4"]',
    1,
    1,
    0
)
ON DUPLICATE KEY UPDATE
    vram = VALUES(vram),
    architecture = VALUES(architecture),
    description = VALUES(description),
    tags = VALUES(tags),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    del_flag = 0,
    update_time = NOW();

-- 2. 地区：beijing-b
INSERT INTO gpu_region (
    region_code,
    region_name,
    sort_order,
    status,
    del_flag
) VALUES (
    'beijing-b',
    '北京B区',
    1,
    1,
    0
)
ON DUPLICATE KEY UPDATE
    region_name = VALUES(region_name),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    del_flag = 0,
    update_time = NOW();

-- 3. 专区：B01
INSERT INTO gpu_zone (
    zone_code,
    zone_name,
    sort_order,
    status,
    del_flag
) VALUES (
    'B01',
    'B01专区',
    1,
    1,
    0
)
ON DUPLICATE KEY UPDATE
    zone_name = VALUES(zone_name),
    sort_order = VALUES(sort_order),
    status = VALUES(status),
    del_flag = 0,
    update_time = NOW();

-- 4. 资源主表：l4-002
INSERT INTO gpu_resource (
    resource_no,
    machine_id,
    machine_uuid,
    region_code,
    zone_code,
    spec_id,
    gpu_count,
    gpu_driver,
    cuda_version,
    cache_optimized,
    cpu_cores,
    cpu_model,
    memory_size,
    system_disk,
    data_disk,
    expandable,
    rentable_until,
    status,
    del_flag
)
SELECT
    'RES-DEMO-L4-002',
    'l4-002',
    'l4-002',
    'beijing-b',
    'B01',
    s.id,
    1,
    '550.54.15',
    '<= 12.4',
    1,
    4,
    'Xeon(R) Platinum 8470Q',
    '16Gi',
    '30Gi',
    '50Gi',
    '100Gi',
    '2027-12-31',
    1,
    0
FROM gpu_resource_spec s
WHERE s.model = 'L4'
ON DUPLICATE KEY UPDATE
    resource_no = VALUES(resource_no),
    machine_id = VALUES(machine_id),
    region_code = VALUES(region_code),
    zone_code = VALUES(zone_code),
    spec_id = VALUES(spec_id),
    gpu_count = VALUES(gpu_count),
    gpu_driver = VALUES(gpu_driver),
    cuda_version = VALUES(cuda_version),
    cache_optimized = VALUES(cache_optimized),
    cpu_cores = VALUES(cpu_cores),
    cpu_model = VALUES(cpu_model),
    memory_size = VALUES(memory_size),
    system_disk = VALUES(system_disk),
    data_disk = VALUES(data_disk),
    expandable = VALUES(expandable),
    rentable_until = VALUES(rentable_until),
    status = VALUES(status),
    del_flag = 0,
    update_time = NOW();

-- 5. 价格：只维护 hourly，匹配 curl 成功参数
INSERT INTO gpu_resource_price (
    resource_id,
    billing_type,
    unit_price,
    discount_price,
    discount_rate,
    currency,
    del_flag
)
SELECT
    r.id,
    'hourly',
    2.50,
    1.88,
    '7.5',
    'CNY',
    0
FROM gpu_resource r
WHERE r.machine_uuid = 'l4-002'
ON DUPLICATE KEY UPDATE
    unit_price = VALUES(unit_price),
    discount_price = VALUES(discount_price),
    discount_rate = VALUES(discount_rate),
    currency = VALUES(currency),
    del_flag = 0,
    update_time = NOW();

-- 6. 库存：1 / 1
INSERT INTO gpu_resource_stock (
    resource_id,
    available_count,
    total_count,
    last_sync_time,
    del_flag
)
SELECT
    r.id,
    1,
    1,
    NOW(),
    0
FROM gpu_resource r
WHERE r.machine_uuid = 'l4-002'
ON DUPLICATE KEY UPDATE
    available_count = VALUES(available_count),
    total_count = VALUES(total_count),
    last_sync_time = VALUES(last_sync_time),
    del_flag = 0,
    update_time = NOW();

-- 7. 查询 resourceId，用于打开 computeRent 页面
SELECT
    r.id AS resource_id,
    r.resource_no,
    r.machine_id,
    r.machine_uuid,
    s.model,
    s.vram,
    r.region_code,
    r.zone_code,
    p.billing_type,
    p.unit_price,
    p.discount_price,
    st.available_count,
    st.total_count
FROM gpu_resource r
JOIN gpu_resource_spec s ON r.spec_id = s.id
LEFT JOIN gpu_resource_price p ON p.resource_id = r.id AND p.del_flag = 0
LEFT JOIN gpu_resource_stock st ON st.resource_id = r.id AND st.del_flag = 0
WHERE r.machine_uuid = 'l4-002'
  AND r.del_flag = 0;
```

---

## 执行后的验证步骤

1. 执行上面的 SQL。
2. 查看最后一条查询结果里的 `resource_id`。
3. 打开：

```text
http://localhost:4173/#/computeRent?resourceId=<resource_id>
```

4. 选择基础镜像：

```text
PyTorch 2.1 / CUDA 12.1 / Python 3.10
```

5. 点击：

```text
创建并开机
```

6. 预期后端会调用：

```text
{gpu-pod.base-url}/api/v1/gpu/pod/create
```

并传入与 curl 验证参数一致的 L4 资源信息。

---

## 注意事项

### 1. 租户配置必须一致

外部 GPU Pod 接口要求 body 中的 `tenantId` 与 Bearer Token 中的租户一致。

后端现在会从配置补租户：

```yaml
gpu-pod:
  tenant-id: tenant_001
  tenant-name: AI科技公司
```

请确认当前运行环境的 `application-*.yml` 中配置正确。

### 2. 资源只维护 hourly

这条演示资源只插入了：

```text
billing_type = hourly
```

因此创建页只会展示按小时/按量计费。这样可以避免选择按日、按周、按月后出现“价格不存在”。

### 3. 镜像地址使用 runtime

前端静态镜像中 PyTorch 2.1 已改为：

```text
acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch:2.5.0-cuda12.4-cudnn9-runtime
```

与已验证成功的 curl 参数一致。
