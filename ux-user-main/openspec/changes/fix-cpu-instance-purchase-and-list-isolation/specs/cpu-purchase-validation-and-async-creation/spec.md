## ADDED Requirements

### Requirement: Validate CPU purchase input before submission
The system SHALL validate all CPU purchase fields before calling the purchase API.

#### Scenario: Required selections are missing
- **WHEN** region, zone, or CPU instance type is empty
- **THEN** the system prevents submission
- **AND** identifies the missing field to the user

#### Scenario: Zone does not belong to the selected region
- **WHEN** the selected zone is not present in the selected region's returned zone list
- **THEN** the system prevents submission
- **AND** explains that the zone must belong to the selected region

#### Scenario: Numeric values are outside the agreed ranges
- **WHEN** system disk is outside `20–2048 GiB`, data disk is outside `0–8192 GiB`, count is outside `1–10`, or duration is outside `1–36` for a prepaid mode
- **THEN** the system prevents submission
- **AND** displays the corresponding range

#### Scenario: Numeric fields are cleared or are not finite integers
- **WHEN** system disk, data disk, count, or required prepaid duration is empty, nonnumeric, non-finite, or fractional
- **THEN** the system SHALL show a field-level validation error before entering submission loading
- **AND** no purchase API request is sent

#### Scenario: No data disk is requested
- **WHEN** the user explicitly enters zero for the data disk
- **THEN** validation SHALL accept zero as no data disk
- **AND** an empty field SHALL remain distinct from the explicit zero value

#### Scenario: Catalog is loading or the billing mode is invalid
- **WHEN** the current region catalog has not loaded successfully or the billing mode is outside `on_demand`, `monthly`, and `yearly`
- **THEN** the system SHALL prevent submission and display an actionable reason
- **AND** stale catalog responses SHALL NOT validate a specification from a previously selected region

#### Scenario: Billing mode is on demand
- **WHEN** the user selects `on_demand`
- **THEN** duration is not required for submission
- **AND** the request uses the selected billing mode

#### Scenario: Instance type is not from the current CPU catalog
- **WHEN** the selected instance type ID is absent from the loaded pure CPU catalog
- **THEN** the system prevents submission
- **AND** does not send the purchase request

### Requirement: Show accepted CPU creation without blocking on cloud completion
The system SHALL transition to a visible creating state as soon as a purchase request is accepted.

#### Scenario: Upstream provisioning takes five minutes
- **WHEN** a valid purchase requires a long-running upstream provisioning call
- **THEN** the portal SHALL persist a tenant-scoped creation operation and return a queryable operation ID without waiting for that call to complete
- **AND** the frontend SHALL display `提交中` until acceptance is confirmed and `创建中` after confirmation
- **AND** the operation SHALL remain queryable after page reload or service restart

#### Scenario: User navigates away before an instance ID exists
- **WHEN** an accepted creation operation has no upstream instance ID yet
- **THEN** the CPU list SHALL display its pending creation record keyed by operation ID
- **AND** a returned instance ID SHALL replace that placeholder through explicit correlation without a duplicate row

#### Scenario: Purchase is accepted with instance records
- **WHEN** the purchase response has `success: true` and returns instance records
- **THEN** the system displays each returned instance as creating unless a terminal status is already returned
- **AND** shows the instance ID and a message that cloud creation may take several minutes
- **AND** refreshes the CPU instance list

#### Scenario: Purchase request times out after submission may have started
- **WHEN** the network request times out or loses connection after submission begins
- **THEN** the system does not claim that creation failed
- **AND** tells the user to check the CPU instance list for the current status
- **AND** recovery uses the original request key rather than issuing a second purchase

#### Scenario: Duplicate purchase submission
- **WHEN** the same tenant submits or retries the same request key while its purchase is pending
- **THEN** the portal SHALL return the same creation operation
- **AND** SHALL NOT send an additional cloud purchase request

### Requirement: Display CPU creation success and failure states
The system SHALL display terminal CPU instance status and service messages without requiring the purchase page to remain open.

#### Scenario: CPU creation completes
- **WHEN** a refreshed CPU instance changes to `running` or `stopped`
- **THEN** the list displays the corresponding status label
- **AND** enables only the lifecycle actions valid for that status

#### Scenario: CPU creation fails
- **WHEN** the authoritative creation operation or CPU instance explicitly reports `failed` or `error`
- **THEN** the list displays a failure status
- **AND** shows the most specific available failure reason
- **AND** does not present the instance as successfully created

#### Scenario: Polling is interrupted or the observation window expires
- **WHEN** status querying fails temporarily or a bounded polling window ends before a terminal state
- **THEN** the system SHALL retain the last confirmed creation state and allow manual refresh
- **AND** SHALL NOT infer provisioning failure from the polling failure or timeout

### Requirement: Preserve business validation results
The system SHALL display HTTP 200 responses with `success: false` as business failures.

#### Scenario: CPU purchase business validation fails
- **WHEN** the purchase response contains `success: false` and no successfully created instance
- **THEN** the system displays the returned message and check results
- **AND** does not show a success or created-instance confirmation

#### Scenario: Multi-instance purchase partially succeeds
- **WHEN** a purchase returns both created instances and per-instance errors
- **THEN** the system SHALL retain the successfully created records and display their states
- **AND** separately show failure counts and reasons without retrying the entire purchase
