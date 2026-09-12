## Context

GPU component images are maintained in `gpu_component`. The current model stores the runtime description in a single `base_image` string such as `PyTorch 2.8.0 + Python 3.10 + Ubuntu 22.04 + CUDA 12.8`. The admin component page displays this as one wide column and currently supports only query and list operations.

The same component records are consumed by the PC rental mirror APIs and by GPU resource component binding. Those consumers expect the legacy `baseImage` label and `imageAddress`, so the redesign must improve admin maintainability without breaking existing display or order flows.

## Goals / Non-Goals

**Goals:**
- Store base image runtime details as explicit fields: component version, Python version, OS name, OS version, and CUDA version.
- Backfill existing component rows by splitting current `base_image` values on `+` and normalizing known field prefixes.
- Add admin create, edit, and status toggle operations for component records.
- Redesign the component table so operators can scan each runtime field independently.
- Preserve a compatible combined `baseImage` value for existing mirror and resource binding flows.

**Non-Goals:**
- Do not redesign the user rental mirror selection experience.
- Do not add GPU-model-specific component filtering.
- Do not remove legacy `baseImage` from public responses in this change.
- Do not introduce a separate normalized table for runtime dimensions.

## Decisions

1. Keep one `gpu_component` row per image address and add structured runtime columns.

   Add nullable columns such as `component_version`, `python_version`, `os_name`, `os_version`, and `cuda_version` to `gpu_component`. This keeps the existing row identity, unique image address constraint, sort order, status, and resource binding behavior intact.

   Alternative considered: create a child table for arbitrary image fields. That would support unlimited dimensions, but it would complicate table rendering, validation, and the simple CRUD flow operators need here.

2. Preserve `base_image` as the compatibility label.

   Keep storing or deriving `base_image` as the combined display string from the structured fields. Backend responses should continue returning `baseImage` for PC rental mirror APIs and existing admin/resource code. This avoids a breaking API change while allowing new admin columns to use the structured values.

   Alternative considered: remove `base_image` and force all consumers to compose the label. That is cleaner long term, but it increases the blast radius and risks breaking existing rental pages.

3. Parse existing values with tolerant normalization.

   Migration and service fallback parsing should split on `+`, trim whitespace, and detect known prefixes case-insensitively: `Python`, `Ubuntu`/OS tokens, and `CUDA`/`cuda`. The first token may include component name and version; when it starts with the existing `component_name`, the remainder becomes `component_version`.

   Alternative considered: require manual data correction before migration. That is safer for perfect data quality, but it delays the UI improvement and is unnecessary for the current seed data format.

4. Follow existing GPU admin CRUD patterns.

   Add `/system/gpu/cluster/component/save`, `/update`, and `/status` endpoints using the same request style as GPU spec and region management. The admin page should reuse Element Plus dialog/table patterns already present in sibling GPU management pages.

   Alternative considered: use RESTful `PUT`/`PATCH` endpoints. Existing GPU admin APIs use `POST` plus `GET status`, so following local convention reduces frontend and backend inconsistency.

## Risks / Trade-offs

- Inconsistent historical `base_image` formats may parse imperfectly -> keep `baseImage` unchanged where parsing is ambiguous and allow admins to correct structured fields through edit.
- Duplicating structured fields and `base_image` can drift -> compose `baseImage` from structured fields on save/update, or centralize composition in the service before persistence and response mapping.
- Frontend and backend live in separate subprojects -> implementation must update `console-main` backend and `ux-admin-main` admin UI together, with smoke testing through the component page.
- Existing mirror consumers rely on `baseImage` text -> keep response compatibility and add regression checks around `/pc/gpu-rent/mirror/list` and mirror versions.

## Migration Plan

1. Add a Flyway migration that appends structured runtime columns to `gpu_component` and backfills them from existing `base_image` strings.
2. Update backend entity, VO, edit DTO, service mapping, validation, and CRUD/status endpoints.
3. Update admin API helpers and component management page.
4. Verify existing records display correctly in the redesigned table and can be edited.
5. Verify PC mirror APIs still return populated `baseImage`, `imageAddress`, version, CUDA version, and Python version.

Rollback: keep the existing `base_image` column untouched so older code can continue reading it if the new UI/API changes need to be reverted.

## Open Questions

- Should OS be stored as separate `os_name` and `os_version`, or is a single `os_version` label enough for all future images?
- Should `component_version` be required for components like Miniconda where the first token may not carry a conventional version?
