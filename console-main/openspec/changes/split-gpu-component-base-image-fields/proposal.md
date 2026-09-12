## Why

GPU 组件管理 currently stores the base image as one concatenated string, which makes the admin table hard to scan and prevents reliable filtering, editing, or validation of the runtime fields. The component page also lacks create, edit, and disable actions, so operators cannot maintain the image catalog from the UI.

## What Changes

- Redesign GPU component data so the base image is stored as structured fields split from the `+`-separated runtime string.
- Add component create, update, and status toggle capabilities for the admin component management page.
- Update the admin component table to display separate columns for framework/component version, Python version, operating system, and CUDA version.
- Keep a compatible `baseImage` value for existing resource binding and PC rental mirror flows that still display or consume the combined label.
- Add a database migration to add the structured fields and backfill existing component rows from the current `base_image` values.

## Capabilities

### New Capabilities
- `gpu-component-management`: Admins can manage GPU component image catalog records with structured base image fields while preserving compatibility with existing mirror consumers.

### Modified Capabilities

None.

## Impact

- Backend database migration for `gpu_component` structured runtime columns and existing data backfill.
- Backend entity, VO, edit DTO, service, and controller endpoints under `/system/gpu/cluster/component/**`.
- Existing PC rental mirror APIs that read `GpuComponentEntity` must continue returning `baseImage`, `imageAddress`, and mirror version values.
- Admin frontend API module `ux-admin-main/src/api/gpuCluster.ts` and page `ux-admin-main/src/views/gpu/cluster/component/index.vue`.
- Regression surface includes GPU resource component binding and user rental base image display.
