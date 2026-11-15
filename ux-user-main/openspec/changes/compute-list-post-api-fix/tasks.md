## Tasks for Compute List POST API Migration

### T1: Analyze Current Implementation
- [ ] Locate all API calls in `computeListNew` page
- [ ] Identify GET requests that need to be migrated to POST
- [ ] Document current filter parameter passing mechanism
- [ ] Identify why filters are not being applied

**Files to check:**
- `src/view/computeList/new.vue`
- `src/api/` (GPU market related APIs)

### T2: Update API Interface Definitions
- [ ] Update API function signatures to accept POST body parameters
- [ ] Change HTTP method from GET to POST
- [ ] Update TypeScript interfaces for request/response types

**Files to modify:**
- `src/api/` - GPU market API files

### T3: Modify Compute List Page Component
- [ ] Update component to build filter object for POST body
- [ ] Ensure all filter conditions are included in request body
- [ ] Handle filter state changes and re-fetch data

**Files to modify:**
- `src/view/computeList/new.vue`

### T4: Fix Filter Functionality
- [ ] Debug why billing method filter is not working
- [ ] Debug why region filter is not working
- [ ] Debug why GPU model filter is not working
- [ ] Debug why GPU quantity filter is not working
- [ ] Ensure filter values are correctly passed in POST body

**Files to check:**
- Frontend: `src/view/computeList/new.vue`
- Backend: Check if backend properly parses POST body for filters

### T5: Test and Validate
- [ ] Test POST requests are working correctly
- [ ] Test all filter combinations
- [ ] Verify response data is correct
- [ ] Test error handling
- [ ] Verify backward compatibility

### T6: Update Documentation (if needed)
- [ ] Update API documentation
- [ ] Update component documentation

---

## Implementation Notes

### API Migration Example

**Before (GET):**
```typescript
// API definition
export function getGpuList(params: {
  page: number;
  size: number;
  billingType?: string;
  region?: string;
  gpuModel?: string;
  gpuCount?: number;
}) {
  return request({
    url: '/api/pc/gpu/market/list',
    method: 'get',
    params
  });
}
```

**After (POST):**
```typescript
// API definition
export function getGpuList(data: {
  filters: {
    billingType?: string;
    region?: string;
    gpuModel?: string;
    gpuCount?: number;
  };
  pagination: {
    page: number;
    size: number;
  }
}) {
  return request({
    url: '/api/pc/gpu/market/list',
    method: 'post',
    data
  });
}
```

### Filter Fix Approach

1. **Ensure filters are reactive** - Use Vue's reactive system properly
2. **Build filter object before API call** - Don't rely on query params
3. **Reset pagination when filters change** - Go back to page 1
4. **Debounce rapid filter changes** - Avoid excessive API calls
