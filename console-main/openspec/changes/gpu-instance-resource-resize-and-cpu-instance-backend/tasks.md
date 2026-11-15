## 1. Adapter foundation

- [x] 1.1 Add adapter configuration for external base URL and reuse existing timeout/token settings.
- [x] 1.2 Add tenant-safe request/header and URL helpers for protected and public upstream calls.
- [x] 1.3 Add JSON request/response DTOs and `success` business-response normalization.

## 2. GPU resource adapter

- [x] 2.1 Implement options, resize, task list, task detail, and bandwidth upstream methods.
- [x] 2.2 Expose `/pc/instance-resource` controller routes for interfaces 64–68.
- [x] 2.3 Preserve upstream 200 business failures and 403/404 outcomes.

## 3. CPU instance adapter

- [x] 3.1 Implement public regions and instance-types upstream methods.
- [x] 3.2 Implement tenant-scoped CPU list, detail, purchase, start, stop, and release methods.
- [x] 3.3 Expose `/pc/cpu-instance` controller routes for interfaces 69–76.
- [x] 3.4 Preserve `origin=cpu_instance`, checks, instance records, and error messages.

## 4. Compatibility and verification

- [x] 4.1 Add the reserved `RESOURCE_RESIZE` order type constant without payment behavior.
- [x] 4.2 Verify existing GPU Pod controller/client behavior is unchanged.
- [ ] 4.3 Add adapter tests for tenant forwarding, business failures, and CPU/GPU route isolation.
- [x] 4.4 Run the backend compile and relevant tests.
