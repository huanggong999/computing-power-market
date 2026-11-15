## 1. API contracts and shared types

- [x] 1.1 Confirm the portal backend adapter paths, response envelope, tenant query parameters, and authorization forwarding for document interfaces 64–76.
- [x] 1.2 Add TypeScript types for GPU resize options, resize requests, resize task history/progress, bandwidth requests/results, CPU regions, CPU instance types, CPU instances, and CPU purchase results.
- [x] 1.3 Add API functions for GPU options, resize submission, resize task list/detail, bandwidth adjustment, CPU regions, CPU instance types, CPU list/detail, CPU purchase, CPU start/stop, and CPU release.
- [x] 1.4 Define shared status, backing, origin, capability, limit, and error normalization helpers without changing existing GPU API behavior.

## 2. GPU resource resize

- [x] 2.1 Add a resource management entry to the GPU instance detail/more-actions UI.
- [x] 2.2 Implement the resize options loading state and render current resources, capabilities, limits, `backing`, and service notes.
- [x] 2.3 Implement combined data disk and CPU/memory resize form validation, including disk expansion-only rules, CPU scale-up-only rules, target instance type requirements, and min/max limits.
- [x] 2.4 Implement resize submission with duplicate-submit protection and display the accepted `task_id`.
- [x] 2.5 Implement resize task polling with terminal success/failed handling, timeout/stop behavior, step messages, and detail/options refresh.
- [x] 2.6 Implement resize task history loading and display task parameters, status, steps, and messages.
- [x] 2.7 Add backing-specific guidance for Volcano and cluster instances, including stop/restart and Kubernetes-layer notices.

## 3. GPU public bandwidth adjustment

- [x] 3.1 Add a separate bandwidth adjustment control driven by `bandwidth_change` and `limits.bandwidth_mbps`.
- [x] 3.2 Implement target Mbps validation, synchronous bandwidth submission, success feedback, and instance detail refresh.
- [x] 3.3 Handle unavailable independent public IP, out-of-range values, and service errors without changing local state optimistically.

## 4. GPU/CPU instance management tabs

- [x] 4.1 Add GPU/CPU tabs to the existing instance management page while preserving the current GPU tab behavior.
- [x] 4.2 Add CPU list state, pagination, loading, empty state, refresh, and error handling separate from the GPU Pinia state.
- [x] 4.3 Add CPU-specific table columns for CPU cores, memory, disks, billing mode, public/private IP, and SSH information.
- [x] 4.4 Route row detail, start, stop, and release actions by instance type/origin so CPU rows never call GPU Pod endpoints.
- [x] 4.5 Add CPU detail presentation and irreversible release confirmation with cloud-host/data deletion warning.

## 5. Independent CPU purchase

- [x] 5.1 Add the CPU purchase route, menu entry, page shell, and navigation from the instance management page.
- [x] 5.2 Implement region and zone loading from interface 69 and CPU-only instance type loading from interface 70.
- [x] 5.3 Implement CPU purchase form fields for region, zone, instance type, billing mode (`on_demand`, `monthly`, `yearly`), duration, system disk, data disk, and count.
- [x] 5.4 Validate that only service-returned pure CPU `instanceTypeId` values can be submitted.
- [x] 5.5 Implement long-running purchase submission, loading/progress messaging, business failure checks, success result display, and CPU list refresh.
- [x] 5.6 State clearly that purchased CPU instances do not join a GPU/Kubernetes cluster; keep public IP purchase options out of this change.

## 6. Backend integration and compatibility

- [x] 6.1 Add or verify backend adapter endpoints for the external resource resize and CPU APIs, including tenant isolation and authorization forwarding.
- [x] 6.2 Add source/origin mapping needed to distinguish GPU Pod, cluster-backed, and independent CPU instances.
- [x] 6.3 Add the reserved `orderType=RESOURCE_RESIZE` contract without enabling resource-change payment in this change.
- [x] 6.4 Verify existing GPU list, detail, lifecycle, monitoring, and creation flows remain backward compatible.

## 7. Verification

- [x] 7.1 Add unit tests for capability/limit validation, disk shrink rejection, billing mapping, backing/origin routing, and task terminal-state handling.
- [x] 7.2 Add API mock or contract tests for document interfaces 64–76, including HTTP 200 business failures and 404/403 cases.
- [x] 7.3 Run `npx vue-tsc --noEmit` and `npm run build` in `ux-user-main`.
- [x] 7.4 Browser-verify GPU resize/bandwidth entries, GPU name/password/release dialogs, and GPU/CPU tabs; CPU lifecycle rules are unit-tested because the current tenant CPU list returns no instances.
