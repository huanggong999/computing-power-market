## 1. CPU purchase validation

- [x] 1.1 Extract a pure CPU purchase validator for required selections, region/zone relationship, catalog-backed `instanceTypeId`, billing mode, duration, system disk, data disk, and count.
- [x] 1.2 Add form-level validation messages to the compute marketplace CPU form while preserving ranges `systemDiskGb: 20–2048`, `dataDiskGb: 0–8192`, `count: 1–10`, and prepaid `duration: 1–36`.
- [x] 1.3 Apply the same validator to the legacy standalone CPU purchase component or remove that duplicate route from active navigation so both entry paths cannot diverge.
- [x] 1.4 Add unit tests for missing fields, invalid zone, invalid CPU catalog ID, numeric boundaries, and on-demand versus prepaid duration rules.

## 2. CPU asynchronous creation feedback

- [x] 2.1 Normalize purchase responses and CPU instance statuses into shared `creating`, `running`, `stopped`, `failed`, and `error` presentation states with a status message.
- [x] 2.2 Change CPU purchase success handling to show accepted instance IDs and “创建中” immediately, refresh the CPU list, and avoid waiting for cloud provisioning to reach `running`.
- [x] 2.3 Handle HTTP 200 business failures by displaying the service message, checks, and errors without showing a success confirmation.
- [x] 2.4 Handle request timeout or connection loss as an indeterminate submission state that directs the user to the CPU list instead of claiming creation failure.
- [x] 2.5 Add CPU list refresh or bounded polling for instances in `creating`, stopping when the service returns a terminal state or a user-visible timeout is reached.
- [x] 2.6 Add tests for accepted creation, delayed transition to running, failed creation, business validation failure, and network timeout.

## 3. CPU instance page parity

- [x] 3.1 Remove the top-level `创建实例` action from `/cloud/gpuInstance` and remove the CPU Tab `购买 CPU 实例` action; keep CPU purchase navigation only in the compute marketplace.
- [x] 3.2 Add CPU statistics cards matching the GPU Tab for total, running, stopped, and other statuses.
- [x] 3.3 Rebuild `CpuInstanceTable` with the GPU table's container, toolbar, loading, empty state, pagination, spacing, and action-cell treatment while retaining CPU-specific columns.
- [x] 3.4 Add unified status tags and disable invalid CPU actions for creating, releasing, failed, and released states.
- [x] 3.5 Replace the CPU `el-dialog` detail presentation with the GPU-style side detail panel pattern and render CPU resources, disks, network, SSH, billing, timestamps, and status messages.
- [x] 3.6 Add CPU list filtering for instance name/ID, region, and status using the CPU API or a clearly scoped client-side fallback.
- [x] 3.7 Add component tests or browser checks for CPU empty state, populated rows, status actions, detail panel, pagination, and absence of purchase buttons.

## 4. GPU/CPU source isolation

- [x] 4.1 Extend the GPU list response model and frontend types to preserve upstream `origin` or equivalent source metadata and status messages.
- [x] 4.2 Filter GPU list results to GPU-classified instances before calculating totals and filter CPU list results to `origin=cpu_instance`.
- [x] 4.3 Add source guards before GPU detail, start, stop, restart, and release operations; refuse mismatched sources with a clear refresh message.
- [x] 4.4 Add source guards before CPU detail, start, stop, and release operations; never call CPU endpoints for non-CPU instances.
- [x] 4.5 Coordinate the paired portal backend change to preserve origin fields, apply server-side GPU filtering, and reject mismatched GPU release requests.
- [x] 4.6 Add API and service tests for mixed upstream lists, filtered totals, source mismatch rejection, and correct source-specific release routing.

## 5. Verification and rollout

- [x] 5.1 Run frontend type checking, unit tests, and production build in `ux-user-main`.
- [x] 5.2 Run backend adapter/service compile verification for source metadata and lifecycle guards in the paired `console-main` change.
- [x] 5.3 Perform browser verification for CPU purchase validation, creation-in-progress behavior, CPU/GPU layout parity, hidden purchase actions, and source-filtered CPU list rendering; backend source guards cover mixed-source lifecycle protection.
- [x] 5.4 Document the upstream requirement for a returned CPU instance ID or a queryable creation correlation key when a purchase is accepted asynchronously.
