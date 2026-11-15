## Context

`console-main` uses Spring controllers under `/pc/*`, `GpuPodApiClient`, `GpuPodTokenManager`, and `GpuPodTenantProvider` to call external services. The v2.1 document defines `/api/v1/instances/*` and `/api/v1/cpu/*` contracts with tenant query parameters, bearer authentication, and business failures represented by HTTP 200 plus `success: false`.

## Goals / Non-Goals

**Goals:**

- Provide stable portal routes for all documented interfaces 64–76.
- Resolve tenant identity from `SecurityContext`, never from client supplied tenant IDs.
- Reuse the existing external service base URL, token manager, timeouts, and JSON handling.
- Preserve the external payload fields needed by the frontend, including `backing`, `origin`, capabilities, limits, checks, and task steps.
- Keep legacy GPU Pod routes and response behavior unchanged.

**Non-Goals:**

- No payment, fee calculation, or `RESOURCE_RESIZE` order execution.
- No database persistence for transient resize tasks.
- No direct external-service calls from the browser.

## Decisions

### 1. Add dedicated adapter client and controllers

Create a focused client for `/instances/{id}/resize`, `/instances/{id}/bandwidth`, and `/cpu/*`, plus two PC controllers with stable `/pc/instance-resource` and `/pc/cpu-instance` prefixes. This keeps the legacy GPU Pod client untouched and makes API ownership obvious.

### 2. Tenant and authorization forwarding

The controller obtains the current tenant from `GpuPodTenantProvider`. The adapter appends `tenant_id` for protected calls and creates headers through the existing token manager. Public CPU catalog calls omit tenant and bearer requirements. Client supplied tenant query parameters are ignored or rejected.

### 3. Response envelope

Adapters return `Result.success(payload)` for transport-successful calls, including payloads whose `success` field is false, so the frontend can inspect the documented business result. Transport failures are normalized using the project's existing exception handling. 404/403 from the upstream remain error responses.

### 4. DTO strategy

Use Jackson/JSON tree payloads or small DTOs for the documented stable envelope, with `JsonNode` for nested fields that may evolve. Request DTOs explicitly whitelist documented fields to prevent tenant or authorization injection.

### 5. Future order extension

Add a shared `RESOURCE_RESIZE` order type constant only. No order creation, balance deduction, or payment endpoint is called by resize adapters.

## Risks / Trade-offs

- [Upstream response fields evolve] → Preserve unknown nested fields with tree payloads and add contract tests for required fields.
- [Token expiry during long CPU purchase] → Reuse the existing token manager and return a normalized service error; the frontend can retry.
- [Tenant leakage through path/query input] → Tenant IDs come only from `SecurityContext`; upstream URLs are built from encoded path variables.
- [Legacy route regression] → Keep existing classes unchanged and add focused adapter tests.

## Migration Plan

1. Deploy the additive adapter client, DTOs, and controllers.
2. Configure the adapter to use the existing GPU scheduler base URL and token settings.
3. Verify tenant and authorization forwarding in lower environments.
4. Release the frontend against the new `/pc/*` routes.
5. Roll back by disabling the new routes or reverting the additive classes; legacy GPU routes remain available.

## Open Questions

- Confirm whether the external CPU APIs share the current GPU scheduler base URL or require a separate base URL before production configuration.
