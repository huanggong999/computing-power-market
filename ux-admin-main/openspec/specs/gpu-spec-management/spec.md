# gpu-spec-management Specification

## Purpose
TBD - created by archiving change gpu-resource-management-ui. Update Purpose after archive.
## Requirements
### Requirement: Spec list view
The system SHALL display a paginated list of GPU specifications with columns: id, model, vram, architecture, description, tags, sortOrder, status.

#### Scenario: Admin views spec list
- **WHEN** admin navigates to GPU规格管理 page
- **THEN** system displays paginated table with all specs

### Requirement: Spec creation
The system SHALL allow creating a new GPU spec with fields: model, vram, architecture, description, tags (array), sortOrder, status.

#### Scenario: Admin creates spec
- **WHEN** admin clicks "新增规格" and fills the form
- **THEN** new GPU spec is created

### Requirement: Spec editing
The system SHALL allow editing an existing GPU spec.

#### Scenario: Admin edits spec
- **WHEN** admin clicks "编辑" on a spec row
- **THEN** dialog opens pre-filled with spec data

### Requirement: Spec deletion
The system SHALL allow deleting a GPU spec with confirmation.

#### Scenario: Admin deletes spec
- **WHEN** admin clicks "删除" and confirms
- **THEN** spec is removed if no resources reference it

