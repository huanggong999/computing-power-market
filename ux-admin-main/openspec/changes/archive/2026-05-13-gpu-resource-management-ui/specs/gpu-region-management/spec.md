## ADDED Requirements

### Requirement: Region list view
The system SHALL display a list of GPU regions with columns: regionCode, regionName, sortOrder, status.

#### Scenario: Admin views region list
- **WHEN** admin navigates to 地区管理 page
- **THEN** system displays table with all regions

### Requirement: Region creation
The system SHALL allow creating a new region with fields: regionCode, regionName, sortOrder, status.

#### Scenario: Admin creates region
- **WHEN** admin clicks "新增地区" and fills the form
- **THEN** new region is created

### Requirement: Region editing
The system SHALL allow editing an existing region.

#### Scenario: Admin edits region
- **WHEN** admin clicks "编辑" on a region row
- **THEN** dialog opens pre-filled with region data

### Requirement: Region deletion
The system SHALL allow deleting a region with confirmation.

#### Scenario: Admin deletes region
- **WHEN** admin clicks "删除" and confirms
- **THEN** region is removed if no resources reference it
