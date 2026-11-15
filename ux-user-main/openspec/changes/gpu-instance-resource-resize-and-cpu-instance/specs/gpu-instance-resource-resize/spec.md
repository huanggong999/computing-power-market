## ADDED Requirements

### Requirement: GPU instance resource options
The system SHALL load the resource resize options for a GPU instance before displaying an editable resource form.

#### Scenario: Options load successfully
- **WHEN** the user opens resource management for a GPU instance
- **THEN** the system requests the instance resize options
- **AND** displays the current CPU, memory, system disk, data disk, bandwidth, capabilities, limits, and backing source returned by the service

#### Scenario: An option is unavailable
- **WHEN** the options response marks a capability as false
- **THEN** the corresponding control SHALL be hidden or disabled
- **AND** the user SHALL see the service-provided reason or limitation when available

### Requirement: GPU disk and CPU memory resize
The system SHALL allow the user to submit supported data disk and CPU/memory changes using the service-defined limits.

#### Scenario: Submit data disk expansion
- **WHEN** the user enters a data disk target greater than the current data disk and within the returned maximum
- **THEN** the system submits `diskGb` to the resize endpoint
- **AND** displays the returned task status instead of treating acceptance as completion

#### Scenario: Reject data disk shrink
- **WHEN** the user enters a data disk target less than or equal to the current capacity
- **THEN** the system SHALL prevent submission
- **AND** explain that data disks support expansion only

#### Scenario: Submit CPU and memory resize
- **WHEN** the user selects an allowed CPU/memory target and, for a Volcano instance, a target `instanceTypeId`
- **THEN** the system submits the target CPU, memory, and instance type fields in one resize request
- **AND** shows the stop/restart notice when `requires_stop_for_spec` is true

### Requirement: GPU resize task tracking
The system SHALL track an accepted resize task until it reaches a terminal status and SHALL provide recent task history.

#### Scenario: Resize task succeeds
- **WHEN** task polling returns `success`
- **THEN** the system displays the completed steps and message
- **AND** refreshes the instance detail and resource options

#### Scenario: Resize task fails
- **WHEN** task polling returns `failed`
- **THEN** the system displays the failure message and completed steps
- **AND** re-enables the resource form without claiming that the requested resources were applied

#### Scenario: User opens task history
- **WHEN** the user opens the resize history section
- **THEN** the system requests the instance resize task list
- **AND** displays task parameters, status, steps, and messages

### Requirement: GPU public bandwidth adjustment
The system SHALL allow bandwidth changes through the dedicated bandwidth endpoint when the instance has an adjustable independent public IP.

#### Scenario: Increase or decrease bandwidth
- **WHEN** `bandwidth_change` is true and the user enters a target within the returned bandwidth limits
- **THEN** the system submits `bandwidthMbps`
- **AND** displays the synchronous result and refreshed bandwidth value

#### Scenario: Bandwidth change is unavailable
- **WHEN** the options response marks `bandwidth_change` as false
- **THEN** the system SHALL disable bandwidth editing
- **AND** explain that the instance is not bound to an adjustable independent public IP

### Requirement: GPU backing-specific guidance
The system SHALL use the returned `backing` value to explain how a resource change is applied.

#### Scenario: Volcano-backed instance
- **WHEN** `backing` is `volc`
- **THEN** the system explains that CPU/memory changes may stop the instance and that the cloud service performs the change

#### Scenario: Cluster-backed instance
- **WHEN** `backing` is `cluster`
- **THEN** the system explains that the change is applied at the Kubernetes resource layer
- **AND** respects `cpu_scale_up_only` and other returned capabilities
