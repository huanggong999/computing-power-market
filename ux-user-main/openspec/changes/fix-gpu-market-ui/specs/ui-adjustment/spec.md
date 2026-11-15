## ADDED Requirements

### Requirement: Page header displays without redundant title
The system SHALL display the "算力市场-new" page header area with sort options only, without a large "算力市场" title or subtitle text.

#### Scenario: User views marketplace page
- **WHEN** user navigates to `/computeListNew`
- **THEN** the page header shows only the sort options (价格, 显存, 型号) and sort direction toggle
- **AND** no large title or subtitle text is displayed in the header area

### Requirement: Filter panel uses readable font size
The system SHALL display filter panel options with a font size no smaller than the standard body text size used elsewhere on the page.

#### Scenario: User reads filter options
- **WHEN** user views the filter panel on the left side of the marketplace page
- **THEN** all checkbox labels, radio labels, and section headers are displayed at a readable size
- **AND** the font size is consistent with the rest of the page content
