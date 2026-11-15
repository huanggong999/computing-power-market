## ADDED Requirements

### Requirement: Page displays GPU marketplace list
The system SHALL display a GPU marketplace list page at `/computeListNew` with page title "算力市场", subtitle "海量GPU资源，按需租用，秒级启动", and a main content area containing a filter sidebar and GPU card list.

#### Scenario: User navigates to marketplace page
- **WHEN** user clicks "算力市场-new" in the top navigation bar
- **THEN** the browser navigates to `/computeListNew`
- **AND** the page displays the page title, subtitle, sort options, filter sidebar, and GPU card list

### Requirement: Sorting functionality
The system SHALL provide sorting options by price, vram, and model, with ascending/descending toggle.

#### Scenario: User sorts by price ascending
- **WHEN** user selects "价格" sort option with ascending order
- **THEN** the GPU list is sorted by price from low to high

#### Scenario: User toggles sort order
- **WHEN** user clicks the sort order toggle button
- **THEN** the sort direction switches between ascending and descending
- **AND** the GPU list re-sorts accordingly

### Requirement: Pagination functionality
The system SHALL paginate the filtered GPU list with configurable page size (12, 24, 48 per page).

#### Scenario: User changes page
- **WHEN** user clicks a different page number
- **THEN** the GPU list updates to show GPUs for the selected page
- **AND** the page scrolls to the top smoothly

#### Scenario: User changes page size
- **WHEN** user selects a different page size from the dropdown
- **THEN** the GPU list updates to display the new number of items per page
- **AND** the current page resets to 1

### Requirement: Empty state handling
The system SHALL display an empty state message when no GPUs match the current filters.

#### Scenario: No matching GPUs after filtering
- **WHEN** user applies filters that match no GPUs
- **THEN** the GPU list area displays "暂无符合条件的GPU实例" with a button to clear filters
- **AND** clicking the clear filters button resets all filters and shows all GPUs
