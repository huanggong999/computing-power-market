## Why

Admins need a single operational view under cluster management to understand GPU cluster health, capacity usage, and card-level runtime metrics. The scheduler already exposes a consolidated monitor overview endpoint, but the admin console has no menu entry or page that renders the summary, cluster, node, and GPU-card layers.

## What Changes

- Add a new `集群监控` menu item under `AI算力中心 > 集群管理`.
- Add a backend admin proxy endpoint for the scheduler monitor overview API so the frontend uses the existing authenticated backend route instead of calling the scheduler directly.
- Add a frontend API helper for the monitor overview data.
- Add an admin cluster monitoring dashboard page that renders:
  - overall summary metrics from `summary`
  - GPU model breakdown
  - cluster-level resource cards or rows from `clusters`
  - expandable node-level resource details
  - GPU card realtime utilization, memory, temperature, and power metrics when available
- Show graceful degraded state when `metrics_source` is `none` or Prometheus/DCGM data is unavailable while preserving quantity metrics.

## Capabilities

### New Capabilities
- `gpu-cluster-monitor-dashboard`: Admins can access a cluster monitoring dashboard with summary, cluster, node, and GPU-card metrics sourced through the backend scheduler proxy.

### Modified Capabilities

None.

## Impact

- Backend scheduler client `GpuSchedulerApiClient` gains a monitor overview request for `/api/v1/admin/monitor/overview` using existing base URL and admin auth-key handling.
- Backend admin controller `SystemGpuResourceController` exposes a new `/system/gpu/cluster/monitor/overview` endpoint.
- Database migration adds the `集群监控` menu below existing `gpuCluster` menu data using MySQL-compatible conditional insert patterns.
- Admin frontend API module `ux-admin-main/src/api/gpuCluster.ts` gains a monitor overview helper.
- Admin frontend adds `ux-admin-main/src/views/gpu/cluster/monitor/index.vue` and relies on dynamic backend menu routing.
- Runtime behavior depends on scheduler availability; when Prometheus/DCGM metrics are unavailable, realtime card data may be empty while capacity counts still render.
