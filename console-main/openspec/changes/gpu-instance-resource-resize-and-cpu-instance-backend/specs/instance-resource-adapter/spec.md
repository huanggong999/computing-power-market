## ADDED Requirements

### Requirement: Proxy GPU resource resize contracts
The portal SHALL expose tenant-safe adapter endpoints for documented GPU interfaces 64–68 and preserve `success`, task, capability, limit, backing, and bandwidth fields.

#### Scenario: Forward resize options
- **WHEN** an authenticated tenant requests options for an instance
- **THEN** the adapter forwards the tenant ID and bearer token upstream
- **AND** returns the documented options payload

#### Scenario: Preserve asynchronous resize acceptance
- **WHEN** the upstream accepts a resize
- **THEN** the adapter returns `task_id` and `status`
- **AND** does not report the task as completed

#### Scenario: Preserve business validation failure
- **WHEN** upstream returns HTTP 200 with `success: false`
- **THEN** the adapter returns that business payload without converting it to success

#### Scenario: Forward bandwidth adjustment
- **WHEN** an authenticated tenant submits a bandwidth target
- **THEN** the adapter forwards `bandwidthMbps` and tenant identity
- **AND** returns the synchronous result

### Requirement: Isolate tenants and authorization
The adapter SHALL derive tenant identity from the authenticated portal session and SHALL forward the external bearer token for protected calls.

#### Scenario: Client supplies a different tenant
- **WHEN** a request includes a tenant query parameter different from the session tenant
- **THEN** the adapter ignores or rejects the supplied value
- **AND** upstream receives only the session tenant

#### Scenario: Upstream denies access
- **WHEN** upstream returns 403 or 404
- **THEN** the adapter exposes an equivalent error outcome
