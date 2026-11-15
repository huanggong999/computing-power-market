## ADDED Requirements

### Requirement: Market metadata comes from managed data
The system SHALL load `/computeListNew` filter metadata from backend-managed GPU market data instead of hardcoded frontend options.

#### Scenario: Load metadata successfully
- **WHEN** a user opens `/computeListNew`
- **THEN** the page SHALL request GPU market metadata from `/pc/gpu/market/meta`
- **AND** the filter panel SHALL display enabled regions, zones, GPU models, GPU counts, and billing types from the response

#### Scenario: Metadata reflects disabled records
- **WHEN** an admin disables or deletes a region, zone, or GPU spec
- **THEN** the metadata response SHALL exclude that disabled or deleted item

### Requirement: Market list comes from managed resources
The system SHALL load `/computeListNew` GPU cards from backend-managed GPU resources instead of static frontend data.

#### Scenario: Load first page successfully
- **WHEN** a user opens `/computeListNew`
- **THEN** the page SHALL request `/pc/gpu/market/list` with the default billing type, sort, and pagination parameters
- **AND** the page SHALL render GPU cards from the returned list
- **AND** the pagination SHALL display the returned total count

#### Scenario: Only listed resources appear
- **WHEN** a GPU resource is not in status `1`
- **THEN** `/pc/gpu/market/list` SHALL exclude that resource from the user market list

### Requirement: Market filters are applied by backend query
The system SHALL apply billing type, region, zone, GPU model, and GPU count filters in the backend query.

#### Scenario: Apply filters
- **WHEN** a user selects a region, zone, GPU model, GPU count, or billing type
- **THEN** the next list request SHALL include the selected filters
- **AND** the response SHALL contain only resources matching all selected filters

#### Scenario: Clear filters
- **WHEN** a user clicks the clear-filter action
- **THEN** the page SHALL reset filters to defaults
- **AND** the list SHALL reload from the backend using default filters

### Requirement: Market sorting is applied safely
The system SHALL support backend sorting by price, VRAM/spec order, and model using a whitelist of supported sort fields and directions.

#### Scenario: Sort by price ascending
- **WHEN** a user selects price sort with ascending order
- **THEN** `/pc/gpu/market/list` SHALL return matching resources ordered by the selected billing type price from low to high

#### Scenario: Reject unsupported sort input
- **WHEN** a request provides an unsupported sort field or direction
- **THEN** the backend SHALL fall back to the default safe ordering rather than interpolating arbitrary SQL

### Requirement: Admin-maintained resources drive market display
The system SHALL ensure resources saved in the admin GPU resource management page can be displayed in `/computeListNew` when listed.

#### Scenario: Admin creates listed resource
- **WHEN** an admin creates a GPU resource with enabled spec, region, stock, price, and status `1`
- **THEN** the resource SHALL be eligible to appear in `/pc/gpu/market/list`

#### Scenario: Admin updates stock and price
- **WHEN** an admin updates a resource's stock or price
- **THEN** the next market list response SHALL reflect the updated available count, total count, and selected billing type price

### Requirement: Billing type values are consistent
The system SHALL use `hourly`, `daily`, `weekly`, and `monthly` as canonical billing type values across admin frontend, backend, database, and user frontend.

#### Scenario: Query hourly price
- **WHEN** the user page requests the list with `billingType=hourly`
- **THEN** the backend SHALL match prices stored with `billing_type='hourly'`

#### Scenario: Admin saves prices
- **WHEN** an admin saves resource price configuration
- **THEN** the submitted billing type values SHALL use lowercase canonical values
