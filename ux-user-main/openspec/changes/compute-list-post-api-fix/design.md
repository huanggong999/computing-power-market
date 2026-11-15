## Context

The computeListNew page is a new compute resource listing page that fetches data from backend APIs. Currently:
1. All API calls use GET requests, which limits parameter complexity
2. Filter conditions (billing method, region, GPU model, GPU quantity) are not being properly applied

## Goals / Non-Goals

**Goals:**
- Migrate all API calls from GET to POST requests
- Fix filter condition passing to backend
- Ensure proper serialization of complex filter objects in request body

**Non-Goals:**
- No UI/UX changes to the page layout
- No backend API endpoint changes (only request method change)

## Decisions

1. **Use POST instead of GET**
   - POST allows sending complex filter objects in request body
   - Better for extensibility with future filter options
   - No URL length limitations

2. **Request Body Structure**
   - Use standard JSON format: `{ filters: { billingType, region, gpuModel, gpuCount }, pagination: { page, size } }`
   - Matches existing backend DTO structure

3. **API Endpoint Path**
   - Keep same endpoint path, only change method from GET to POST
   - Example: `GET /api/pc/gpu/market/list` → `POST /api/pc/gpu/market/list`

## Risks / Trade-offs

- [Risk] Backend may not yet support POST for these endpoints → Verify backend compatibility first
- [Risk] Caching behavior changes (GET responses may be cached, POST typically not) → Acceptable for this use case

## Migration Plan

1. Update frontend API interface definitions
2. Modify component to use POST with request body
3. Test filter functionality
4. Verify backward compatibility

## Open Questions

- None at this time
