## ADDED Requirements

### Requirement: Preserve instance source metadata
The system SHALL preserve the upstream instance source metadata through the GPU list response and frontend model.

#### Scenario: Upstream returns an origin field
- **WHEN** the upstream instance list includes `origin` or an equivalent source marker
- **THEN** the portal response and frontend model retain that marker

#### Scenario: Upstream omits origin but exposes GPU resources
- **WHEN** an upstream item has no origin but has an explicit GPU resource
- **THEN** the portal SHALL classify it through a documented legacy GPU compatibility rule
- **AND** records that the classification used the GPU resource fallback

#### Scenario: Explicit CPU origin conflicts with GPU resource fields
- **WHEN** an item explicitly declares `origin=cpu_instance` and also has GPU-like fields
- **THEN** the system SHALL exclude it from the GPU list and reject GPU lifecycle operations
- **AND** an explicit origin takes precedence over legacy field inference

### Requirement: Keep GPU and CPU lists mutually exclusive
The system SHALL prevent independent CPU instances from appearing in the GPU list and GPU instances from appearing in the CPU list.

#### Scenario: GPU list contains mixed upstream items
- **WHEN** the upstream response contains GPU and independent CPU instances
- **THEN** the GPU response includes only GPU-classified instances
- **AND** source filtering occurs before pagination and total calculation over the complete matching set
- **AND** a filtered current-page length is not reported as the full dataset total

#### Scenario: CPU list contains mixed or malformed items
- **WHEN** the CPU endpoint returns an item whose origin is not `cpu_instance`
- **THEN** the frontend excludes that item from the CPU list

### Requirement: Protect lifecycle operations by source
The system SHALL validate instance source before invoking a type-specific lifecycle endpoint.

#### Scenario: GPU action targets a CPU instance
- **WHEN** a GPU detail, start, stop, restart, or release action receives a CPU-origin instance
- **THEN** the system refuses the GPU operation
- **AND** displays a source mismatch message

#### Scenario: CPU action targets a GPU instance
- **WHEN** a CPU action receives an instance whose origin is not `cpu_instance`
- **THEN** the system refuses the CPU operation
- **AND** does not call the independent CPU endpoint

#### Scenario: Valid release action
- **WHEN** the instance source matches the selected list and the user confirms release
- **THEN** the system calls only the matching source-specific release endpoint
- **AND** refreshes only the affected instance list after success

#### Scenario: Direct GPU API call with mismatched or unknown source
- **WHEN** a client directly invokes a GPU lifecycle endpoint with a CPU-origin or unverified instance ID
- **THEN** the portal backend SHALL resolve tenant ownership and authoritative source before forwarding any mutation
- **AND** refuse the call without invoking the upstream release endpoint when source verification fails

#### Scenario: Batch operation contains a CPU instance
- **WHEN** a GPU batch operation includes a CPU-origin or unverified instance
- **THEN** the system SHALL refuse the mismatched item and report its outcome
- **AND** no GPU upstream mutation is sent for that item
