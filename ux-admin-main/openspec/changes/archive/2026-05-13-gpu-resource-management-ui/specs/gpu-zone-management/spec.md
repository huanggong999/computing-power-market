## ADDED Requirements

### Requirement: Zone list view
The system SHALL display a list of GPU zones with columns: zoneCode, zoneName, sortOrder, status.

#### Scenario: Admin views zone list
- **WHEN** admin navigates to 专区管理 page
- **THEN** system displays table with all zones

### Requirement: Zone creation
The system SHALL allow creating a new zone with fields: zoneCode, zoneName, sortOrder, status.

#### Scenario: Admin creates zone
- **WHEN** admin clicks "新增专区" and fills the form
- **THEN** new zone is created

### Requirement: Zone editing
The system SHALL allow editing an existing zone.

#### Scenario: Admin edits zone
- **WHEN** admin clicks "编辑" on a zone row
- **THEN** dialog opens pre-filled with zone data

### Requirement: Zone deletion
The system SHALL allow deleting a zone with confirmation.

#### Scenario: Admin deletes zone
- **WHEN** admin clicks "删除" and confirms
- **THEN** zone is removed if no resources reference it
