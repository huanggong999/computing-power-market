2026-07-10: Compute rent create failed before calling `/api/v1/gpu/pod/create` because `GpuPodApiClient` always requested a tenant access token and the scheduler returned `401 Unauthorized` for `/api/v1/auth/token`.

Fix: configure `gpu-pod.admin-auth-key: gpuscheduler-admin-2026` and make `GpuPodApiClient` use `authKey` query authentication when that value is present, skipping the token manager. Keep token authentication as the fallback when no admin auth key is configured.
