## Context

Cluster management currently has backend-driven menu entries for cluster list, node pool management, and component management. The frontend dynamic router resolves each `sys_menu.component` value to a Vue file under `ux-admin-main/src/views/**/*.vue`, so adding a new page requires both a backend menu migration and a matching frontend component path.

Scheduler integration already flows through `GpuSchedulerApiClient` and `SystemGpuResourceController` under `/system/gpu/cluster/**`. The scheduler base URL and admin auth key are backend configuration concerns, and existing frontend cluster pages call backend admin endpoints instead of direct scheduler `/api/v1/**` URLs.

The new scheduler monitor endpoint returns a three-layer payload in one request: `summary`, `clusters`, and nested `nodes` with optional `gpu_cards`. Capacity counts must remain useful even when Prometheus/DCGM metrics are unavailable.

## Goals / Non-Goals

**Goals:**
- Add a `集群监控` menu below `AI算力中心 > 集群管理`.
- Proxy scheduler monitor overview data through a backend admin endpoint using existing scheduler configuration and auth-key handling.
- Render an operational admin dashboard showing summary, cluster, node, and GPU-card metrics.
- Preserve the data口径 that quantity metrics are based on Kubernetes occupancy while realtime card metrics come from Prometheus/DCGM when available.
- Gracefully handle `metrics_source = none` and empty `gpu_cards` without hiding capacity counts.

**Non-Goals:**
- Do not redesign existing cluster list, node pool, or component management pages.
- Do not implement Prometheus/DCGM collection in the admin console; the scheduler remains the source of monitor data.
- Do not persist monitor snapshots in the admin backend database.
- Do not expose `authKey` configuration to the browser.

## Decisions

1. Use a backend proxy endpoint for monitor overview.

   Add `GET /system/gpu/cluster/monitor/overview` to `SystemGpuResourceController`, backed by a new `GpuSchedulerApiClient.getMonitorOverview()` method that calls scheduler path `/api/v1/admin/monitor/overview`. This follows existing cluster APIs and keeps `gpu.scheduler.base-url` and `gpu-pod.admin-auth-key` on the server.

   Alternative considered: call `/api/v1/admin/monitor/overview?authKey=...` directly from the frontend. That would expose scheduler connectivity and auth concerns to the browser and diverge from existing admin cluster API patterns.

2. Return the scheduler JSON shape without introducing backend DTO mapping for this view.

   The monitor payload is nested, dashboard-oriented, and already includes frontend-friendly structures such as `summary`, `gpu_models`, `clusters`, `nodes`, and `gpu_cards`. Returning `JSONObject` through the backend proxy keeps the implementation small and avoids DTO churn for a read-only dashboard.

   Alternative considered: create typed Java VO classes for every nested shape. That improves compile-time structure but adds a large mapping surface for data the backend does not transform.

3. Add menu data with a MySQL-compatible conditional insert.

   Create a Flyway migration that locates the existing `gpuCluster` parent and inserts the `集群监控` child when it does not already exist. Use the repository's current `INSERT ... SELECT ... WHERE NOT EXISTS` style rather than MySQL-incompatible DDL shortcuts.

   Alternative considered: update frontend static routes only. The current admin menu is dynamic and permission-driven, so a static route would not make the menu visible through normal navigation.

4. Build the dashboard as a dense Element Plus operational page.

   The page should use existing admin styling conventions: cards for individual metric blocks, Element Plus tables/descriptions/tags/progress where appropriate, and expandable cluster/node sections. The first screen should be the usable monitoring dashboard, not a landing or explanatory page.

   Alternative considered: place everything into one large table. That is compact, but it makes summary and nested card-level GPU metrics harder to scan.

5. Treat missing realtime card metrics as partial data, not page failure.

   When `metrics_source` is `none` or a node has no `gpu_cards`, the dashboard displays capacity and allocation counts and shows an empty or warning state only for realtime card metrics.

   Alternative considered: show a full-page error when Prometheus is unavailable. That would hide useful Kubernetes capacity data that the endpoint still provides.

## Risks / Trade-offs

- Scheduler endpoint unavailable -> Backend proxy should catch failures consistently with existing scheduler client behavior and frontend should show an empty/error state instead of a broken page.
- Response field naming may vary between snake_case and camelCase -> Frontend helpers should read known scheduler keys defensively, following existing cluster pages' `displayValue` style.
- Large clusters may create a heavy nested DOM -> Render clusters as expandable sections and only show node/card details inside expanded content.
- MySQL menu migration could duplicate rows if path/parent matching is loose -> Match by resolved `gpuCluster` parent id and `path = 'monitor'` with `NOT EXISTS`.
- Leaving the response as `JSONObject` reduces backend type safety -> Limit the proxy to pass-through monitor data and put display normalization in the frontend page.

## Migration Plan

1. Add backend scheduler client and controller proxy for monitor overview.
2. Add a Flyway migration to insert the `集群监控` menu under `gpuCluster`.
3. Add the frontend API helper and `gpu/cluster/monitor/index.vue` page.
4. Verify the menu appears after backend menu data is migrated and user permissions include the new menu.
5. Verify the dashboard renders normal monitor data and degraded `metrics_source = none` data.

Rollback: remove or disable the `sys_menu` row for `path = 'monitor'` under `gpuCluster`; the backend proxy is read-only and can remain unused if the menu is hidden.

## Open Questions

- Should the dashboard auto-refresh periodically, or should it use manual refresh only for the first version?
- Should cluster cards default to collapsed for large installations, or expand the first cluster by default?
