## 1. Database and Model

- [x] 1.1 Add a Flyway migration for `gpu_component` structured runtime columns: `component_version`, `python_version`, `os_name`, `os_version`, and `cuda_version`.
- [x] 1.2 Backfill the new columns from existing `base_image` values by splitting on `+`, trimming tokens, and recognizing component version, Python, OS, and CUDA tokens.
- [x] 1.3 Update `GpuComponentEntity` with the new table fields.
- [x] 1.4 Add component edit/request model fields and update `GpuComponentVO` to expose structured runtime fields plus compatible `baseImage`.

## 2. Backend Component Management

- [x] 2.1 Extend `GpuComponentService` with save/update and status update methods.
- [x] 2.2 Implement component create/update validation, image address uniqueness checks, status defaults, and centralized `baseImage` composition in `GpuComponentServiceImpl`.
- [x] 2.3 Add `/system/gpu/cluster/component/save`, `/system/gpu/cluster/component/update`, and `/system/gpu/cluster/component/status` endpoints to `SystemGpuResourceController`.
- [x] 2.4 Update component list mapping so it returns structured runtime fields and fills missing fields from legacy `baseImage` where needed.
- [x] 2.5 Verify PC rental mirror mapping still returns compatible `baseImage`, `imageAddress`, Python version, CUDA version, and version labels.

## 3. Admin Frontend

- [x] 3.1 Add component save, update, and status API helpers in `ux-admin-main/src/api/gpuCluster.ts`.
- [x] 3.2 Redesign `ux-admin-main/src/views/gpu/cluster/component/index.vue` columns to show component name, component version, Python, operating system, CUDA, image address, status, description, and operations.
- [x] 3.3 Add an "新增组件" table header action and an Element Plus dialog form for create/edit.
- [x] 3.4 Add row operation buttons for edit and enable/disable status toggle.
- [x] 3.5 Ensure the dialog composes and submits structured fields while preserving existing list search by component name and status.

## 4. Verification

- [x] 4.1 Run backend compile or targeted tests for `console-main` GPU component changes.
- [x] 4.2 Run frontend type check/build or targeted verification for `ux-admin-main`.
- [ ] 4.3 Smoke test the component management page at `/gpu/gpuCluster/component` for list, add, edit, and status toggle.
- [ ] 4.4 Smoke test PC rental mirror list/version APIs to confirm existing `baseImage` consumers remain compatible.
