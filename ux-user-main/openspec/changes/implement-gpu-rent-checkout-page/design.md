# Spec: gpu-rent-checkout

## Overview

GPU 租用确认功能，提供用户选择镜像、配置计费方式、查看费用并提交租用订单的完整流程。

## Change Summary

本规范定义 GPU 租用确认页面的功能需求：
- 展示 GPU 硬件配置信息
- 提供镜像环境选择
- 支持计费方式配置
- 实时费用计算
- 订单创建和提交

## Functional Requirements

### Requirement: Display GPU Configuration

The system SHALL display detailed GPU resource configuration information in a read-only format.

#### Scenario: Load GPU Details
- **WHEN** the checkout page loads with a valid resourceId
- **THEN** the system fetches and displays:
  - GPU model and VRAM
  - Region and zone information
  - CPU cores and model
  - Memory size
  - System and data disk configuration
  - CUDA version and GPU driver version

#### Scenario: Invalid Resource
- **WHEN** the resourceId is invalid or resource is unavailable
- **THEN** display an error message with a return button

### Requirement: Mirror Selection

The system SHALL allow users to select a pre-configured environment mirror.

#### Scenario: Load Mirror List
- **WHEN** the checkout page loads
- **THEN** the system loads available mirrors filtered by GPU model

#### Scenario: Select Mirror
- **WHEN** user selects a mirror type (e.g., PyTorch)
- **THEN** the version dropdown updates to show available versions for that mirror

#### Scenario: Switch Mirror Type
- **WHEN** user switches to a different mirror type
- **THEN** the version selection resets and updates accordingly

### Requirement: Billing Configuration

The system SHALL allow users to configure billing method and quantity.

#### Scenario: Select Billing Type
- **WHEN** user selects a billing type (hourly/daily/monthly)
- **THEN** the unit price updates and fee calculation refreshes

#### Scenario: Change Quantity
- **WHEN** user changes the quantity (1 to max available)
- **THEN** the fee calculation updates in real-time

#### Scenario: Validate Stock
- **WHEN** quantity exceeds available stock
- **THEN** display validation error and disable submit

### Requirement: Fee Calculation

The system SHALL calculate and display rent fees in real-time.

#### Scenario: Calculate Base Fee
- **WHEN** billing type or quantity changes
- **THEN** calculate: Unit Price × Quantity × Duration

#### Scenario: Display Fee Breakdown
- **THEN** always display:
  - Unit price
  - Quantity
  - Subtotal
  - Discount (if applicable)
  - Final total

### Requirement: Order Submission

The system SHALL create a rent order when user confirms.

#### Scenario: Validate Before Submit
- **WHEN** user clicks confirm button
- **THEN** validate:
  - Mirror is selected
  - Quantity is valid
  - User has agreed to terms
  - User has sufficient balance (or will use other payment)

#### Scenario: Create Order Success
- **WHEN** all validations pass and API call succeeds
- **THEN** redirect to order detail page or instance console

#### Scenario: Create Order Failure
- **WHEN** API call fails
- **THEN** display error message with retry option

## UI/UX Requirements

### Layout

- **Two-column layout** on desktop (60% / 40%)
- **Single-column stack** on mobile
- **Sticky right column** on scroll (desktop)

### Loading States

- **Skeleton screens** for initial load
- **Spinners** for async operations (fee calculation, submit)
- **Disabled state** for submit button during processing

### Error Handling

- **Inline validation** for form fields
- **Error banners** for page-level errors
- **Retry buttons** for recoverable errors

### Responsive Breakpoints

- **Desktop**: > 1200px (two-column)
- **Tablet**: 768px - 1200px (single-column, larger touch targets)
- **Mobile**: < 768px (single-column, stacked layout)

## API Specifications

### Endpoints

See `src/api/computeRent.ts` for detailed interface definitions.

### Error Codes

- `RESOURCE_NOT_FOUND`: GPU 资源不存在
- `RESOURCE_UNAVAILABLE`: GPU 资源已下架或无库存
- `INSUFFICIENT_BALANCE`: 账户余额不足
- `INVALID_MIRROR`: 镜像不存在或不支持当前 GPU
- `ORDER_CREATION_FAILED`: 订单创建失败

## State Management

### Component State

All state managed within the component using `ref` and `reactive`:

```typescript
// Loading states
const loading = ref(false)
const submitting = ref(false)

// Data
const gpuResource = ref<GpuMarketItem | null>(null)
const mirrorList = ref<MirrorItem[]>([])
const accountInfo = ref<AccountInfo | null>(null)

// Form state
const rentConfig = reactive({
  mirrorId: '',
  versionId: '',
  billingType: 'hourly' as const,
  quantity: 1
})

// Computed
const feeResult = ref<FeeResult | null>(null)
```

### URL State

- `resourceId`: GPU 资源 ID（必需）

## Testing Strategy

### Unit Tests

- Fee calculation logic
- Mirror filtering by GPU model
- Form validation rules

### Integration Tests

- Page load with valid/invalid resourceId
- End-to-end rent flow
- Error handling scenarios

### E2E Tests

- Complete user journey from list to order confirmation
- Responsive layout verification
