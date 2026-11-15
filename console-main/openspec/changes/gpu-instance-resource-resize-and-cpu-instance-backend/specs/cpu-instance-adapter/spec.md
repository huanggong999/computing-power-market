## ADDED Requirements

### Requirement: Proxy independent CPU catalog and lifecycle contracts
The portal SHALL expose tenant-safe adapter endpoints for documented CPU interfaces 69–76 and preserve `origin=cpu_instance` and CPU instance fields.

#### Scenario: Browse public CPU catalog
- **WHEN** a user requests CPU regions or instance types
- **THEN** the adapter forwards the documented public query
- **AND** does not require a tenant ID

#### Scenario: Purchase an independent CPU instance
- **WHEN** an authenticated tenant submits a documented CPU purchase request
- **THEN** the adapter forwards the request with session tenant identity and bearer token
- **AND** returns checks, instances, and errors from upstream

#### Scenario: Route CPU lifecycle actions
- **WHEN** a tenant starts, stops, views, or releases a CPU instance
- **THEN** the adapter calls the matching CPU upstream endpoint
- **AND** never routes the action through GPU Pod endpoints

### Requirement: Preserve business failures
The adapter SHALL preserve HTTP 200 payloads with `success: false` so the portal can display upstream validation messages.

#### Scenario: CPU purchase validation fails
- **WHEN** upstream returns HTTP 200 with `success: false`
- **THEN** the adapter returns the failure message, checks, and empty instance list unchanged
