## ADDED Requirements

### Requirement: GPU card displays instance information
The system SHALL display each GPU instance as a horizontal card containing model name, vram, tags, available card count, CPU info, memory, disk info, room info, price, and GPU configuration options.

#### Scenario: User views GPU card
- **WHEN** the GPU list renders
- **THEN** each card displays the GPU model and vram in the title
- **AND** shows tags (热门, 特价, etc.) with appropriate colors
- **AND** shows specification details in a 2-column grid layout

### Requirement: GPU card shows pricing information
The system SHALL display the current hourly price, original price (with strikethrough), and total price based on selected GPU count.

#### Scenario: User views pricing
- **WHEN** a GPU card renders
- **THEN** it displays the current price in large red text
- **AND** displays the original price with strikethrough
- **AND** shows "元/小时" unit label

### Requirement: GPU count selection
The system SHALL allow users to select the number of GPUs (1 to max available) using radio buttons, with unavailable counts disabled.

#### Scenario: User selects GPU count
- **WHEN** user clicks a GPU count radio button (e.g., 2卡)
- **THEN** the total price updates to reflect the selected count
- **AND** the price calculation shows price × count

#### Scenario: Disabled GPU count
- **WHEN** a GPU has fewer available cards than the configuration option
- **THEN** the corresponding radio button is disabled
- **AND** the user cannot select that count

### Requirement: GPU card action buttons
The system SHALL display action buttons based on GPU status: "租用" (rent) for available, "管理" (manage) for running, and "缺货" (out of stock, disabled) for unavailable.

#### Scenario: Available GPU
- **WHEN** a GPU has status "available"
- **THEN** the card shows a primary "租用" button
- **AND** clicking it emits a rent event with the selected GPU and count

#### Scenario: Running GPU
- **WHEN** a GPU has status "running"
- **THEN** the card shows a "管理" button
- **AND** clicking it navigates to the console page

#### Scenario: Out of stock GPU
- **WHEN** a GPU has status "outofstock"
- **THEN** the card shows a disabled "缺货" button
- **AND** the entire card has reduced opacity (0.7)
