## Why

The GPU marketplace backend has been fully implemented with 6 new tables (gpu_resource, gpu_resource_spec, gpu_resource_price, gpu_resource_stock, gpu_region, gpu_zone) and complete REST APIs. However, there is no admin interface to manage these resources. Administrators need a web UI to perform CRUD operations on GPU resources, specifications, regions, and zones.

## What Changes

- Create a new menu section "算力资源管理" (GPU Resource Management) in the admin sidebar
- Implement 4 management pages:
  - **GPU资源管理** (GPU Resource Management): Full CRUD for GPU resources with drawer-based editing
  - **GPU规格管理** (GPU Spec Management): CRUD for GPU model definitions
  - **地区管理** (Region Management): CRUD for region configuration
  - **专区管理** (Zone Management): CRUD for zone configuration
- Each page includes: list view with search/filter, pagination, add/edit/delete operations
- Resource editing uses drawer component for better UX (40+ fields)
- Integrate with backend APIs at `/system/gpu/*`

## Capabilities

### New Capabilities
- `gpu-resource-management`: Full CRUD operations for GPU resources including price and stock configuration
- `gpu-spec-management`: GPU model specification management (RTX 5090, A800, etc.)
- `gpu-region-management`: Region configuration management (西北B区, 北京B区, etc.)
- `gpu-zone-management`: Zone configuration management (V100专区, A800专区, etc.)

### Modified Capabilities
- None (this is a new module)

## Impact

- **Frontend**: New Vue 3 + Element Plus pages in `ux-admin-main/src/views/gpu/`
- **Router**: New routes added to admin router configuration
- **API**: Consumes existing `/system/gpu/*` endpoints from ai-cloud-system backend
- **Dependencies**: Uses existing Element Plus components (ProTable, Drawer, Form)
