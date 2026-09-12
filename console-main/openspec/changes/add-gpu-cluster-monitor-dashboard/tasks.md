## 1. Backend Monitor Proxy

- [x] 1.1 Add `GpuSchedulerApiClient.getMonitorOverview()` to request scheduler path `/api/v1/admin/monitor/overview` with existing base URL, headers, timeout, and admin auth-key handling.
- [x] 1.2 Add `GET /system/gpu/cluster/monitor/overview` to `SystemGpuResourceController` and return the scheduler overview payload in the existing `Result` wrapper.
- [x] 1.3 Ensure scheduler request failures return a safe response shape or empty object that the frontend can handle without crashing.
- [x] 1.4 Add or update targeted backend tests for monitor overview URL/auth-key construction and controller/client behavior where practical.

## 2. Menu Migration

- [x] 2.1 Add a Flyway migration that resolves the existing `AI算力中心 > 集群管理` menu id.
- [x] 2.2 Insert the `集群监控` child menu with `path = 'monitor'`, `component = 'gpu/cluster/monitor/index'`, and order after existing cluster management children.
- [x] 2.3 Use MySQL-compatible `INSERT ... SELECT ... WHERE NOT EXISTS` logic to avoid duplicate menu rows.

## 3. Admin Frontend API And Page

- [x] 3.1 Add `gpuClusterMonitorOverviewApi` to `ux-admin-main/src/api/gpuCluster.ts` calling `/system/gpu/cluster/monitor/overview`.
- [x] 3.2 Create `ux-admin-main/src/views/gpu/cluster/monitor/index.vue` and load monitor overview data on page entry.
- [x] 3.3 Render summary metrics for cluster count, node count, online/offline nodes, GPU counts, CPU, memory, disk, GPU card count, and average GPU utilization.
- [x] 3.4 Render GPU model breakdown from `summary.gpu_models`.
- [x] 3.5 Render cluster-level metrics from `clusters` with expandable node details.
- [x] 3.6 Render node resource metrics and nested GPU card realtime metrics including utilization, memory, temperature, and power.
- [x] 3.7 Add degraded/empty states for `metrics_source = none`, empty `gpu_cards`, loading, and request failure.
- [x] 3.8 Keep the UI dense and consistent with existing Element Plus admin pages, avoiding unrelated layout redesigns.

## 4. Verification

- [ ] 4.1 Run backend compile or targeted tests for the scheduler client/controller changes.
- [x] 4.2 Run frontend type check/build or targeted verification for `ux-admin-main`.
- [ ] 4.3 Smoke test that the new `集群监控` menu appears under `集群管理` after menu data is migrated.
- [ ] 4.4 Smoke test the monitor dashboard with normal scheduler data, including expanding clusters and viewing node/GPU card details.
- [ ] 4.5 Smoke test degraded monitor data where `metrics_source = none` and `gpu_cards` is empty while quantity metrics still render.
