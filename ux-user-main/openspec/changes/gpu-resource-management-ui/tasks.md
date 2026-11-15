## 1. Router and Menu Setup

- [ ] 1.1 Create `src/router/modules/gpuResource.js` with routes for resource, spec, region, zone
- [ ] 1.2 Add GPU resource management menu to sidebar navigation
- [ ] 1.3 Create `src/views/gpu/` directory structure

## 2. API Layer

- [ ] 2.1 Create `src/api/gpuResource.js` with resource CRUD endpoints
- [ ] 2.2 Create `src/api/gpuSpec.js` with spec CRUD endpoints
- [ ] 2.3 Create `src/api/gpuRegion.js` with region CRUD endpoints
- [ ] 2.4 Create `src/api/gpuZone.js` with zone CRUD endpoints

## 3. GPU Resource Management Page

- [ ] 3.1 Create `src/views/gpu/resource/index.vue` with ProTable and search filters
- [ ] 3.2 Create `src/views/gpu/resource/components/ResourceDrawer.vue` for create/edit
- [ ] 3.3 Implement Basic Info section (machineId, machineUuid, region, zone, rentableUntil, status)
- [ ] 3.4 Implement GPU Config section (spec select, gpuCount, gpuDriver, cudaVersion, cacheOptimized)
- [ ] 3.5 Implement CPU & Memory section (cpuCores, cpuModel, memorySize)
- [ ] 3.6 Implement Storage section (systemDisk, dataDisk, expandable)
- [ ] 3.7 Implement Price Config section with table for hourly/daily/weekly/monthly prices
- [ ] 3.8 Implement Stock Config section (availableCount, totalCount)
- [ ] 3.9 Implement status toggle and delete actions

## 4. GPU Spec Management Page

- [ ] 4.1 Create `src/views/gpu/spec/index.vue` with ProTable
- [ ] 4.2 Create spec add/edit dialog with fields: model, vram, architecture, description, tags, sortOrder, status
- [ ] 4.3 Implement delete with confirmation

## 5. Region Management Page

- [ ] 5.1 Create `src/views/gpu/region/index.vue` with table
- [ ] 5.2 Create region add/edit dialog with fields: regionCode, regionName, sortOrder, status
- [ ] 5.3 Implement delete with confirmation

## 6. Zone Management Page

- [ ] 6.1 Create `src/views/gpu/zone/index.vue` with table
- [ ] 6.2 Create zone add/edit dialog with fields: zoneCode, zoneName, sortOrder, status
- [ ] 6.3 Implement delete with confirmation

## 7. Testing and Integration

- [ ] 7.1 Test all CRUD operations for resources
- [ ] 7.2 Test all CRUD operations for specs
- [ ] 7.3 Test all CRUD operations for regions
- [ ] 7.4 Test all CRUD operations for zones
- [ ] 7.5 Verify integration with backend APIs
