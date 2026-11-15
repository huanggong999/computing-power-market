## Why

The computeListNew page (`/computeListNew`) currently uses GET requests for data fetching, which limits the complexity of request parameters and can cause issues with URL length limitations. Additionally, the filtering functionality (billing method, region, GPU model, GPU quantity) is not working correctly, preventing users from properly filtering the compute resources.

## What Changes

- Change all API calls on the computeListNew page from GET to POST requests
- Fix the filtering logic for:
  - Billing method (计费方式)
  - Region (地区)
  - GPU model (GPU型号)
  - GPU quantity (GPU数量)
- Ensure request parameters are properly serialized in POST body

## Capabilities

### New Capabilities
- `compute-list-post-api`: Migrate compute list page API calls from GET to POST with proper request body handling

### Modified Capabilities
- None (this is purely an implementation fix)

## Impact

- Affected files:
  - `src/view/computeListNew/` - The compute list page components
  - API interface files in `src/api/` related to compute resources
- No database changes
- No breaking changes for users (internal API call change only)
