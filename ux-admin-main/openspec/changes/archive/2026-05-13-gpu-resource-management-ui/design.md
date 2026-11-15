## Context

The GPU marketplace backend (`ai-cloud-system`) has been fully implemented with 6 new tables, MyBatis mappers, services, and REST controllers. The admin frontend (`ux-admin-main`) is a Vue 3 + Element Plus application that currently manages ECS, orders, users, and system settings. There is no UI for managing GPU resources.

The admin UI follows these patterns:
- Router configuration in `src/router/modules/*.js`
- API clients in `src/api/` (Axios-based)
- Pages in `src/views/<module>/`
- ProTable component for list views with search/filter
- Drawer component for create/edit forms

## Goals / Non-Goals

**Goals:**
- Provide full CRUD admin UI for GPU resources, specs, regions, and zones
- Match the existing admin UI style and component patterns
- Integrate with `/system/gpu/*` backend endpoints
- Support resource editing with multi-tab form (basic info, GPU config, CPU/memory, storage, pricing, stock)

**Non-Goals:**
- Real-time inventory dashboard or charts
- Bulk import/export functionality
- Advanced RBAC for GPU management (reuse existing admin role system)
- Changes to the PC user frontend

## Decisions

**Decision 1: Drawer-based editing for GPU resources**
- **Choice**: Use Element Plus Drawer for GPU resource create/edit instead of a new page
- **Rationale**: The resource form has 40+ fields. Drawer keeps list context visible and is consistent with other admin forms in the project

**Decision 2: Reuse ProTable for all list views**
- **Choice**: Use the existing ProTable wrapper component for all 4 management pages
- **Rationale**: Consistent with existing admin patterns, provides built-in pagination, search, and column config

**Decision 3: Separate pages per entity**
- **Choice**: 4 independent pages (resource, spec, region, zone) under `/gpu/*`
- **Rationale**: Each entity has different fields and operations. Separate pages keep complexity manageable

**Decision 4: Simple region/zone management**
- **Choice**: Region and zone use standard table + dialog (not drawer)
- **Rationale**: These entities have only 3-4 fields each. Dialog is sufficient and simpler

## Risks / Trade-offs

- **[Risk]** GPU resource form is complex (40+ fields) and may be overwhelming in a drawer
  → **Mitigation**: Organize into collapsible sections or tabs within the drawer
- **[Risk]** Price configuration needs dynamic rows (4 billing types)
  → **Mitigation**: Use Element Plus Table with inline editing for prices
- **[Trade-off]** No bulk operations in initial implementation
  → Admin can edit resources one at a time
