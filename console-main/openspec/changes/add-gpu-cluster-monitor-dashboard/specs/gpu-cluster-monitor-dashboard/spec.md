## ADDED Requirements

### Requirement: Cluster Monitor Menu
The system SHALL provide a `集群监控` menu entry under `AI算力中心 > 集群管理` that routes to the cluster monitor dashboard page.

#### Scenario: Admin opens cluster management menu
- **WHEN** an admin with access to `AI算力中心 > 集群管理` loads the admin console menu
- **THEN** the menu includes a visible `集群监控` child entry under `集群管理`

#### Scenario: Admin navigates to cluster monitor
- **WHEN** the admin clicks the `集群监控` menu entry
- **THEN** the frontend navigates to a page backed by component path `gpu/cluster/monitor/index`

### Requirement: Backend Monitor Overview Proxy
The system SHALL expose a backend admin endpoint that proxies the scheduler monitor overview data without requiring the frontend to pass the scheduler `authKey` directly.

#### Scenario: Frontend requests monitor overview
- **WHEN** the frontend calls `GET /system/gpu/cluster/monitor/overview`
- **THEN** the backend requests scheduler path `/api/v1/admin/monitor/overview` using configured scheduler base URL and admin auth-key handling
- **AND** the backend returns the scheduler monitor overview payload to the frontend

#### Scenario: Scheduler monitor request fails
- **WHEN** the backend cannot retrieve monitor overview data from the scheduler
- **THEN** the backend returns a safe empty or unsuccessful result that the frontend can render without crashing

### Requirement: Summary Metrics Display
The cluster monitor dashboard SHALL render the top-level `summary` object as operational capacity and health metrics.

#### Scenario: Summary data is available
- **WHEN** monitor overview data contains `summary.cluster_count`, `summary.node_count`, online/offline node counts, GPU counts, CPU, memory, and disk totals
- **THEN** the dashboard displays those values as summary metrics with total, allocated, and available quantities where present

#### Scenario: GPU model breakdown is available
- **WHEN** monitor overview data contains `summary.gpu_models`
- **THEN** the dashboard displays each GPU model with total, allocated, and available counts

### Requirement: Cluster And Node Drilldown
The cluster monitor dashboard SHALL display clusters from the `clusters` array and allow admins to inspect each cluster's nodes.

#### Scenario: Cluster list is available
- **WHEN** monitor overview data contains one or more clusters
- **THEN** the dashboard displays each cluster's name, region, status, node count, GPU total, allocated GPU count, available GPU count, GPU card count, and average GPU utilization

#### Scenario: Admin expands a cluster
- **WHEN** an admin expands or opens a cluster row or card
- **THEN** the dashboard displays that cluster's nodes with node name, region, status, GPU totals, GPU model, CPU, memory, and disk quantities

### Requirement: GPU Card Metrics Display
The cluster monitor dashboard SHALL display realtime GPU card metrics when the scheduler response includes card-level data.

#### Scenario: Node GPU cards are available
- **WHEN** a node contains `gpu_cards` entries
- **THEN** the dashboard displays each GPU card's index, UUID, model, utilization, memory used/free/total, temperature, and power watts

#### Scenario: Realtime metrics are unavailable
- **WHEN** monitor overview data has `metrics_source` set to `none` or a node has an empty `gpu_cards` array
- **THEN** the dashboard still displays summary, cluster, and node quantity metrics
- **AND** the dashboard shows a degraded or empty state for realtime GPU card metrics only

### Requirement: Data口径 Visibility
The cluster monitor dashboard SHALL preserve the scheduler's distinction between quantity metrics and realtime card metrics.

#### Scenario: Quantity metrics are rendered
- **WHEN** the dashboard displays GPU total, allocated, and available quantities
- **THEN** those values are rendered from the scheduler quantity fields that represent Kubernetes actual occupancy

#### Scenario: Metrics source is rendered
- **WHEN** monitor overview data contains `metrics_source`
- **THEN** the dashboard displays the current metrics source state so admins can distinguish Prometheus-backed realtime metrics from degraded data
