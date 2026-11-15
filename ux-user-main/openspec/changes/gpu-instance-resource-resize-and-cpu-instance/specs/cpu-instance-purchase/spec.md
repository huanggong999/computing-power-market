## ADDED Requirements

### Requirement: Browse CPU purchase regions and specifications
The system SHALL provide a CPU purchase page that loads available regions/zones and pure CPU instance types from the public CPU catalog APIs.

#### Scenario: Select a region
- **WHEN** the user opens the CPU purchase page
- **THEN** the system loads available regions and zones
- **AND** loads CPU instance types for the selected region

#### Scenario: Display pure CPU specifications
- **WHEN** the instance type response contains a specification
- **THEN** the system displays its instance type ID, family, CPU cores, CPU model, and memory
- **AND** SHALL NOT present GPU specifications as CPU purchase options

### Requirement: Purchase an independent CPU instance
The system SHALL allow the user to purchase an independent CPU instance without joining a cluster.

#### Scenario: Submit a valid CPU purchase
- **WHEN** the user selects a valid region, zone, CPU `instanceTypeId`, billing mode, duration, system disk, and optional data disk
- **THEN** the system submits the CPU purchase request
- **AND** displays the returned checks and created instance information
- **AND** states that the instance is independent of the GPU/Kubernetes cluster

#### Scenario: Purchase validation fails
- **WHEN** the CPU purchase service returns `success: false`
- **THEN** the system displays the returned business message and checks
- **AND** does not show the instance as successfully created

#### Scenario: Supported billing modes
- **WHEN** the user selects a CPU billing mode
- **THEN** the system supports `on_demand`, `monthly`, and `yearly`
- **AND** sends the selected mode and duration according to the API contract

### Requirement: Separate GPU and CPU instance views
The system SHALL provide GPU and CPU tabs in the existing instance management page and SHALL request each list from its own API chain.

#### Scenario: Open GPU tab
- **WHEN** the user selects the GPU tab
- **THEN** the system loads GPU Pod instances and shows GPU-specific fields and operations

#### Scenario: Open CPU tab
- **WHEN** the user selects the CPU tab
- **THEN** the system loads independent CPU instances
- **AND** shows CPU cores, memory, disks, IP addresses, billing mode, and SSH information
- **AND** SHALL NOT call GPU Pod lifecycle endpoints for those rows

### Requirement: Manage independent CPU instance lifecycle
The system SHALL route CPU instance detail and lifecycle actions to the independent CPU APIs.

#### Scenario: View CPU instance details
- **WHEN** the user opens a CPU instance
- **THEN** the system requests the CPU detail endpoint and displays the returned instance data

#### Scenario: Start or stop a CPU instance
- **WHEN** the user starts or stops a CPU instance
- **THEN** the system calls the CPU start or stop endpoint
- **AND** refreshes the CPU instance status after the operation

#### Scenario: Release a CPU instance
- **WHEN** the user confirms release of a CPU instance
- **THEN** the system calls the CPU delete endpoint
- **AND** clearly warns that the cloud host and its data cannot be recovered
- **AND** removes or marks the instance released after success
