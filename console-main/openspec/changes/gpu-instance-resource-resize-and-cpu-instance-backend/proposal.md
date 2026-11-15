## Why

The user portal needs the documented v2.1 resource resize and independent CPU APIs, but `console-main` currently exposes only the legacy GPU Pod routes. Without a portal adapter, the frontend cannot enforce tenant isolation or forward the scheduler token consistently.

## What Changes

- Add authenticated `/pc/instance-resource/*` adapter routes for GPU resize options, resize submission, task history/detail, and bandwidth changes.
- Add authenticated `/pc/cpu-instance/*` adapter routes for CPU regions, CPU specifications, list/detail, purchase, start, stop, and release.
- Proxy the documented request and response payloads with the portal's existing `Result` envelope while preserving `success: false` business responses.
- Resolve `tenant_id` from the current security context and forward the existing Bearer token to the external service.
- Add source/origin fields needed by the portal to distinguish GPU Pod, cluster-backed, and independent CPU instances.
- Reserve an order type constant for future `RESOURCE_RESIZE` payment integration without charging or creating orders in this change.

## Capabilities

### New Capabilities

- `instance-resource-adapter`: Tenant-safe portal adapters for GPU resize and bandwidth APIs.
- `cpu-instance-adapter`: Tenant-safe portal adapters for independent CPU catalog, purchase, and lifecycle APIs.

### Modified Capabilities

- None.

## Impact

- `ai-cloud-system` controllers, client adapters, DTOs, and configuration.
- Existing GPU Pod APIs remain unchanged; new routes are additive.
- External scheduler/CPU service receives forwarded bearer authentication and tenant query parameters.
- No database migration or payment behavior is introduced.
