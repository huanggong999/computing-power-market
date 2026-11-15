## ADDED Requirements

### Requirement: Present CPU instances with the GPU list page layout
The system SHALL render the CPU Tab with the same page shell and information hierarchy as the GPU Tab.

#### Scenario: Open the CPU Tab
- **WHEN** the user selects CPU instances
- **THEN** the page keeps the shared title, Tab navigation, statistics area, list container, pagination, loading, and empty-state layout
- **AND** the content uses CPU-specific fields

#### Scenario: Show CPU statistics
- **WHEN** the CPU list is loaded
- **THEN** the page displays total, running, stopped, and other-status counts using the same visual treatment as GPU statistics
- **AND** the counts describe the full loaded CPU dataset before text filtering and pagination

### Requirement: Filter and paginate CPU instances
The system SHALL offer CPU instance name/ID, region, and status filters with search, reset, and refresh controls in the GPU-style filter area.

#### Scenario: Search or reset CPU filters
- **WHEN** the user searches by CPU name/ID, region, or status, or resets the filters
- **THEN** the table applies those filters before pagination and returns to page one
- **AND** the pagination total equals the matching instance count

#### Scenario: Refresh while viewing CPU instances
- **WHEN** the user refreshes the CPU list
- **THEN** the selected filters remain applied and an out-of-range page is corrected
- **AND** GPU filters and selected rows do not affect CPU operations

### Requirement: Show useful CPU list fields
The system SHALL display CPU instance identity, state, resources, network, billing, and supported actions in separate readable groups.

#### Scenario: CPU instance is present
- **WHEN** a CPU instance is returned
- **THEN** the row displays instance name and ID, status, CPU and memory, system and data disks, public/private IP, SSH information, and billing mode
- **AND** the row does not display GPU-only fields or VNC actions

#### Scenario: CPU list is empty
- **WHEN** the CPU list contains no instances
- **THEN** the page displays a styled empty state inside the same list container
- **AND** does not show a purchase button on the instance management page

### Requirement: Use GPU-consistent CPU detail presentation
The system SHALL display CPU details in the same side-panel interaction pattern used by GPU details.

#### Scenario: User opens CPU details
- **WHEN** the user selects the detail action for a CPU row
- **THEN** the system opens the shared detail panel pattern
- **AND** displays CPU resources, disks, network, SSH, billing, timestamps, and status message
- **AND** missing data is displayed as unavailable without invented resource usage or credentials

### Requirement: Remove instance-page purchase actions
The system SHALL keep purchase navigation out of the `/cloud/gpuInstance` page.

#### Scenario: User opens the GPU/CPU instance management page
- **WHEN** the page is rendered in either GPU or CPU mode
- **THEN** the page does not display a `创建实例` button
- **AND** does not display a `购买 CPU 实例` button
- **AND** no purchase shortcut is inserted into an empty state or detail panel

#### Scenario: User needs to purchase a CPU instance
- **WHEN** the user navigates to the CPU purchase flow
- **THEN** the entry point is the compute marketplace page
