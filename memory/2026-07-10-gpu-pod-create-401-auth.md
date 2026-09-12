2026-07-10: Compute rent create still failed with `GpuPodApiClient.createGpuPod` returning `401 Unauthorized`.

Root cause: the running backend was calling `/api/v1/gpu/pod/create` without a valid auth mode. External verification showed `/gpu/pod/create?authKey=gpuscheduler-admin-2026` passes authentication and returns request validation errors for incomplete payloads, while the configured `demo-api-key-2024` fails `/auth/token` with `API Key 无效`.

Fix: `GpuPodApiClient` now falls back from `gpu-pod.admin-auth-key` to the existing `gpu.scheduler.auth-key` when building authenticated scheduler URLs. `GpuPodTokenManager` now refreshes the tenant API key via the admin auth key after any token 401/403, even if a configured API key exists. Removed the invalid dev `gpu-pod.api-key` value.

Verification: `/auth/token` succeeds when using the real tenant api_key fetched through the admin endpoint, and `/gpu/pod/create?authKey=...` returns 422 validation errors instead of 401 for an intentionally incomplete payload. Full Maven compile remains blocked by unrelated existing project errors outside the touched classes.
