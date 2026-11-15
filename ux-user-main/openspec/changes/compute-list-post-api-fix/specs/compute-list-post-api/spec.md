## Spec: Compute List POST API Migration

### Overview
Migrate compute list page API calls from GET to POST with proper request body handling for filter conditions.

### Requirements

#### Functional Requirements

**FR1: API Method Migration**
- All API calls on computeListNew page MUST use POST instead of GET
- Request body MUST contain filter parameters in JSON format

**FR2: Filter Parameters**
The following filter conditions MUST be supported in request body:
- `billingType`: String - 计费方式 (e.g., "hourly", "monthly")
- `region`: String - 地区 (e.g., "cn-beijing", "cn-shanghai")
- `gpuModel`: String - GPU型号 (e.g., "A100", "V100")
- `gpuCount`: Number - GPU数量 (e.g., 1, 2, 4, 8)

**FR3: Request Body Structure**
```json
{
  "filters": {
    "billingType": "hourly",
    "region": "cn-beijing",
    "gpuModel": "A100",
    "gpuCount": 2
  },
  "pagination": {
    "page": 1,
    "size": 10
  }
}
```

**FR4: Response Format**
Response structure SHOULD remain unchanged from current GET implementation.

### API Endpoints

| Endpoint | Old Method | New Method | Description |
|----------|-----------|-----------|-------------|
| `/api/pc/gpu/market/list` | GET | POST | 获取算力市场列表 |
| `/api/pc/gpu/market/detail` | GET | POST | 获取算力详情 |
| `/api/pc/gpu/market/regions` | GET | POST | 获取地区列表 |
| `/api/pc/gpu/market/models` | GET | POST | 获取GPU型号列表 |

### Error Handling

**EH1: Invalid Filter Parameters**
- Status Code: 400
- Response: `{ "code": 400, "message": "Invalid filter parameter: billingType" }`

**EH2: Server Error**
- Status Code: 500
- Response: `{ "code": 500, "message": "Internal server error" }`

### Test Cases

**TC1: POST Request with Filters**
```
POST /api/pc/gpu/market/list
Content-Type: application/json

{
  "filters": {
    "billingType": "hourly",
    "region": "cn-beijing"
  }
}
```
Expected: Returns filtered list with 200 status

**TC2: POST Request with All Filters**
```
POST /api/pc/gpu/market/list
Content-Type: application/json

{
  "filters": {
    "billingType": "monthly",
    "region": "cn-shanghai",
    "gpuModel": "A100",
    "gpuCount": 4
  }
}
```
Expected: Returns filtered list with 200 status

**TC3: Invalid Filter Value**
```
POST /api/pc/gpu/market/list
Content-Type: application/json

{
  "filters": {
    "billingType": "invalid"
  }
}
```
Expected: Returns 400 with error message

### Implementation Notes

1. Frontend must update all API call methods from `GET` to `POST`
2. Request body must be serialized as JSON with `Content-Type: application/json` header
3. All filter parameters must be properly validated before sending
4. Error handling must be updated to handle POST-specific errors
