## ADDED Requirements

### Requirement: Filter panel displays filter options
The system SHALL display a filter panel on the left side of the marketplace page with collapsible sections for GPU model, vram, CPU cores, memory, region, price range, and availability status.

#### Scenario: User expands filter sections
- **WHEN** the marketplace page loads
- **THEN** the filter panel displays with GPU model, vram, CPU cores, region, and status sections expanded by default
- **AND** memory and price range sections are collapsed by default

### Requirement: GPU model filter
The system SHALL allow users to filter GPUs by model using checkboxes for RTX 4090, RTX 3090, RTX 4080, RTX 3080, A100, A10, H100, and V100.

#### Scenario: User filters by RTX 4090
- **WHEN** user checks "RTX 4090" in the GPU model filter
- **THEN** the GPU list updates to show only RTX 4090 models
- **AND** the pagination resets to page 1

### Requirement: Price range filter
The system SHALL allow users to filter GPUs by price range using a range slider from 0 to 30 yuan/hour with 0.5 step increments.

#### Scenario: User adjusts price range
- **WHEN** user drags the price slider to set a range of 5-15 yuan/hour
- **THEN** the GPU list updates to show only GPUs within that price range
- **AND** the price range display updates to show the selected values

### Requirement: Status filter
The system SHALL allow users to filter GPUs by availability status with radio buttons for "全部" (all), "可租用" (available), and "运行中" (running).

#### Scenario: User filters by available status
- **WHEN** user selects "可租用" in the status filter
- **THEN** the GPU list updates to show only GPUs with status "available"

### Requirement: Filter reset functionality
The system SHALL provide a "重置" (reset) button in the filter panel header that clears all filter selections.

#### Scenario: User resets all filters
- **WHEN** user clicks the "重置" button
- **THEN** all checkboxes are unchecked
- **AND** the price slider resets to 0-30
- **AND** the status radio resets to "全部"
- **AND** the GPU list shows all GPUs
