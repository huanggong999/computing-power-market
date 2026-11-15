## ADDED Requirements

### Requirement: Resource list view
The system SHALL display a paginated list of GPU resources with columns: resourceNo, machineId, machineUuid, model, vram, regionName, zoneName, cpuCores, cpuModel, memorySize, availableCount/totalCount, price, discountPrice, rentableUntil, status.

#### Scenario: Admin views resource list
- **WHEN** admin navigates to GPU资源管理 page
- **THEN** system displays paginated table with all resources

### Requirement: Resource search and filter
The system SHALL allow filtering resources by: GPU model, region, zone, machineId, status.

#### Scenario: Admin filters by model
- **WHEN** admin selects a GPU model from filter dropdown
- **THEN** list refreshes showing only resources with that model

### Requirement: Resource creation
The system SHALL allow creating a new GPU resource via a drawer form with sections: basic info, GPU config, CPU/memory, storage, price config, stock config, status.

#### Scenario: Admin creates resource
- **WHEN** admin clicks "新增资源" and fills the form with valid data
- **THEN** system creates resource with prices and stock configuration

### Requirement: Resource editing
The system SHALL allow editing an existing GPU resource via the same drawer form.

#### Scenario: Admin edits resource
- **WHEN** admin clicks "编辑" on a resource row
- **THEN** drawer opens pre-filled with resource data

### Requirement: Resource status toggle
The system SHALL allow toggling resource status between 上架/下架/维护中.

#### Scenario: Admin takes resource offline
- **WHEN** admin clicks status toggle
- **THEN** resource status updates and list refreshes

### Requirement: Resource deletion
The system SHALL allow deleting a GPU resource with confirmation.

#### Scenario: Admin deletes resource
- **WHEN** admin clicks "删除" and confirms
- **THEN** resource is removed from the system

### Requirement: Price configuration in resource form
The system SHALL support configuring 4 billing types (hourly, daily, weekly, monthly) each with unitPrice, discountPrice, and discountRate.

#### Scenario: Admin sets hourly price
- **WHEN** admin enters unitPrice=3.00 for hourly billing
- **THEN** system saves price configuration for that billing type
