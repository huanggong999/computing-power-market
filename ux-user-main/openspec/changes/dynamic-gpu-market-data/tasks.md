## 1. Backend API and Query Fixes

- [x] 1.1 Verify `PageQuery` parameter names and adapt `/pc/gpu/market/list` to the frontend pagination contract.
- [x] 1.2 Verify `gpuModels` multi-select request serialization and adapt frontend or backend parsing.
- [x] 1.3 Fix `/pc/gpu/market/meta` current business `code=500` response and ensure it returns only enabled/deleted-safe regions, zones, GPU model stats, GPU counts, and canonical billing types.
- [x] 1.4 Ensure `/pc/gpu/market/list` returns listed resources with joined spec, region, stock, and selected billing type price data.
- [x] 1.5 Fix market list total count if PageHelper metadata is lost after VO conversion.
- [x] 1.6 Keep SQL sorting on a whitelist for `price`, `vram`, and `model` with safe fallback ordering.

## 2. Admin Resource Management Fixes

- [x] 2.1 Update admin GPU resource API methods to match backend routes and HTTP methods.
- [x] 2.2 Change resource drawer region and zone select values from IDs to `regionCode` and `zoneCode`.
- [x] 2.3 Change admin billing type values to lowercase `hourly`, `daily`, `weekly`, and `monthly`.
- [x] 2.4 Align memory, system disk, data disk, and expandable inputs with backend string fields.
- [x] 2.5 Implement or wire resource detail loading so edit mode can populate prices, stock, and full resource configuration.
- [ ] 2.6 Verify creating/updating a listed resource persists resource, stock, and price records correctly.

## 3. User Market Page Dynamic Data

- [x] 3.1 Confirm `src/api/gpuMarket.ts` request and response types match backend `Result<PageResult<GpuMarketItemVO>>`; browser currently shows `dataTotal/list`, not `total/list`.
- [x] 3.2 Update `/computeListNew` pagination parameter names if needed.
- [x] 3.3 Update GPU model array serialization if needed.
- [x] 3.4 Ensure filter reset, sorting, page changes, and page-size changes reload from backend.
- [ ] 3.5 Adjust card price unit when selected billing type is daily, weekly, or monthly if product confirms non-hourly display.
- [x] 3.6 Preserve current empty state and loading behavior using dynamic API results.

## 4. Validation

- [ ] 4.1 Build `lingyang-common` before backend validation.
- [ ] 4.2 Build or run `ai-cloud-system` and verify `/pc/gpu/market/meta` and `/pc/gpu/market/list` with local Redis/MySQL config.
- [x] 4.3 Build or type-check `ux-admin-main` after API and drawer changes.
- [x] 4.4 Build or type-check `ux-user-main` after market page changes.
- [x] 4.5 With browser permission granted, open `http://localhost:4173/#/computeListNew` and verify metadata, filters, sorting, pagination, empty state, and card fields.
