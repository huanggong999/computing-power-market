## ADDED Requirements

### Requirement: Structured Base Image Fields
The system SHALL store GPU component base image runtime details as structured fields instead of only relying on a `+`-concatenated display string.

#### Scenario: Component is saved with structured runtime fields
- **WHEN** an admin saves a GPU component with component name, component version, Python version, operating system name, operating system version, CUDA version, and image address
- **THEN** the system persists each runtime detail in its corresponding component field and stores a compatible combined `baseImage` label for existing consumers

#### Scenario: Component list returns structured runtime fields
- **WHEN** the admin component page requests the component list
- **THEN** each component item includes structured runtime fields and the compatible `baseImage` label

### Requirement: Component Admin Maintenance Actions
The system SHALL allow admins to create, update, and change status for GPU component image records from the component management page.

#### Scenario: Admin creates a component
- **WHEN** an admin submits valid component details with a unique image address
- **THEN** the system creates a GPU component record and the component appears in the list according to sort order

#### Scenario: Admin updates a component
- **WHEN** an admin edits an existing component's runtime fields, image address, description, sort order, or status
- **THEN** the system updates the existing component record and returns the updated values on subsequent list requests

#### Scenario: Admin disables an enabled component
- **WHEN** an admin changes an enabled component status to disabled
- **THEN** the component remains visible in admin management with disabled status and is excluded from enabled-only mirror consumers

### Requirement: Redesigned Component Table
The admin component management page SHALL display base image runtime details as separate table columns instead of one wide `基础镜像` string column.

#### Scenario: Admin views component table
- **WHEN** the component management page renders a component row
- **THEN** the row shows component name, component version, Python version, operating system, CUDA version, image address, status, description, and operation actions in separate table cells

#### Scenario: Admin filters component table
- **WHEN** an admin searches by component name or status
- **THEN** the page filters component records while preserving the structured runtime column display

### Requirement: Existing Mirror Compatibility
The system SHALL preserve existing mirror API compatibility for consumers that use `baseImage`, image address, Python version, or CUDA version values.

#### Scenario: User rental mirror list reads components
- **WHEN** the PC rental mirror list API reads enabled GPU components
- **THEN** each mirror item still includes a non-empty `baseImage` label and `imageAddress`

#### Scenario: User rental mirror versions read components
- **WHEN** the PC rental mirror versions API reads an enabled GPU component
- **THEN** the version response still includes a display version, image address, Python version, CUDA version, and framework mapping derived from the component record

### Requirement: Historical Component Data Backfill
The system SHALL backfill structured runtime fields for existing GPU component records from current `base_image` values.

#### Scenario: Migration parses plus-delimited base image
- **WHEN** an existing `base_image` value contains plus-delimited tokens such as `PyTorch 2.8.0 + Python 3.10 + Ubuntu 22.04 + CUDA 12.8`
- **THEN** the migration populates component version, Python version, operating system name, operating system version, and CUDA version where the tokens can be recognized

#### Scenario: Migration encounters ambiguous tokens
- **WHEN** an existing `base_image` value contains a token that cannot be confidently mapped to a structured field
- **THEN** the system preserves the original `baseImage` label and leaves ambiguous structured fields empty for later admin correction
