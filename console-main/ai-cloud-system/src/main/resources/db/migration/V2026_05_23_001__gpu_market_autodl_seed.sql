INSERT INTO gpu_resource_spec (model, vram, architecture, description, tags, sort_order, status) VALUES
('vGPU-32GB', '32 GB', 'Virtual GPU', 'vGPU 32GB 共享算力实例', '["vGPU"]', 30, 1),
('vGPU-48GB', '48 GB', 'Virtual GPU', 'vGPU 48GB 共享算力实例', '["vGPU","大显存"]', 31, 1),
('RTX 4090D', '24 GB', 'Ada Lovelace', 'NVIDIA RTX 4090D，高性能消费级显卡', '["热门"]', 12, 1),
('RTX 3080x2', '20 GB', 'Ampere', '双 RTX 3080 实例', '["高性价比"]', 22, 1),
('RTX A4000', '16 GB', 'Ampere', 'NVIDIA RTX A4000 专业显卡', '["专业级"]', 24, 1),
('GTX 1080 Ti', '11 GB', 'Pascal', 'NVIDIA GTX 1080 Ti 入门实例', '["入门级"]', 40, 1),
('CPU', '0 GB', 'CPU', '纯 CPU 算力实例', '["CPU"]', 50, 1),
('CPU-close-HT', '0 GB', 'CPU', '关闭超线程 CPU 算力实例', '["CPU"]', 51, 1),
('vGPU-48GB-350W', '48 GB', 'Virtual GPU', '350W vGPU 48GB 实例', '["vGPU"]', 32, 1)
ON DUPLICATE KEY UPDATE vram = VALUES(vram), sort_order = VALUES(sort_order), status = VALUES(status);

UPDATE gpu_region
SET status = CASE WHEN region_code IN ('shanghai-a', 'guangzhou-a', 'shenzhen-a', 'hangzhou-a') THEN 0 ELSE status END
WHERE region_code IN ('shanghai-a', 'guangzhou-a', 'shenzhen-a', 'hangzhou-a');

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status) VALUES
('RES-20260523-001', 'C79机', '47034ea2ad', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'RTX PRO 6000'), 9, '580.119.02', '13.0', 1, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '0 GB', '2027-10-01', 1),
('RES-20260523-002', 'D11机', 'ufu1jbsm98', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'vGPU-48GB'), 8, '580.105.08', '13.0', 1, 25, 'Xeon(R) Platinum 8470Q', '92 GB', '30 GB', '50 GB', '1015 GB', '2027-10-01', 1),
('RES-20260523-003', 'D12机', '58a0ln8rhl', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'vGPU-48GB'), 8, '580.105.08', '13.0', 1, 25, 'Xeon(R) Platinum 8470Q', '92 GB', '30 GB', '50 GB', '3512 GB', '2027-10-01', 1),
('RES-20260523-004', '948机', 'f79448928d', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'RTX PRO 6000'), 9, '580.82.09', '13.0', 0, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '81 GB', '2027-01-01', 1),
('RES-20260523-005', 'A11机', '7d414f84a8', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'RTX PRO 6000'), 9, '580.95.05', '13.0', 1, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '960 GB', '2027-01-01', 1),
('RES-20260523-006', 'B96机', 'yl8aar3mpx', 'northwest-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'vGPU-48GB'), 10, '580.105.08', '13.0', 1, 12, 'Xeon(R) Gold 6459C 基频3.0G', '96 GB', '30 GB', '50 GB', '3021 GB', '2027-01-01', 1),
('RES-20260523-007', 'K09机', 'rtx4090d09', 'beijing-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'RTX 4090D'), 8, '560.94', '12.6', 0, 64, 'AMD EPYC 9654', '512 GB', '1 TB', '4 TB', '8 TB', '2026-12-31', 1),
('RES-20260523-008', 'P32机', 'vgpu32gb32', 'chongqing-a', null, (SELECT id FROM gpu_resource_spec WHERE model = 'vGPU-32GB'), 8, '580.105.08', '13.0', 1, 16, 'Xeon(R) Gold 6459C 基频3.0G', '64 GB', '30 GB', '50 GB', '1200 GB', '2027-01-01', 1),
('RES-20260523-009', 'T88机', 'rtx3080ti88', 'inner-b', null, (SELECT id FROM gpu_resource_spec WHERE model = 'RTX 3080 Ti'), 8, '535.104.05', '12.2', 0, 32, 'AMD EPYC 7742', '128 GB', '50 GB', '200 GB', '1000 GB', '2026-12-31', 1),
('RES-20260523-010', 'CPU01机', 'cpuonly001', 'foshan', null, (SELECT id FROM gpu_resource_spec WHERE model = 'CPU'), 1, null, null, 0, 32, 'Intel Xeon Gold 6338', '128 GB', '50 GB', '200 GB', '500 GB', '2026-12-31', 1)
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), status = VALUES(status), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count);

INSERT INTO gpu_resource_price (resource_id, billing_type, unit_price, discount_price, discount_rate, currency)
SELECT r.id, p.billing_type, p.unit_price, p.discount_price, p.discount_rate, 'CNY'
FROM gpu_resource r
JOIN (
    SELECT '47034ea2ad' machine_uuid, 'hourly' billing_type, 7.97 unit_price, 5.98 discount_price, '7.5' discount_rate UNION ALL
    SELECT '47034ea2ad', 'daily', 191.28, 143.52, '7.5' UNION ALL
    SELECT '47034ea2ad', 'weekly', 1338.96, 1004.22, '7.5' UNION ALL
    SELECT '47034ea2ad', 'monthly', 5740.00, 4305.00, '7.5' UNION ALL
    SELECT 'ufu1jbsm98', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'daily', 72.72, 69.08, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'weekly', 509.04, 483.59, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'monthly', 2181.60, 2072.52, '9.5' UNION ALL
    SELECT '58a0ln8rhl', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'f79448928d', 'hourly', 7.97, 5.98, '7.5' UNION ALL
    SELECT '7d414f84a8', 'hourly', 7.97, 5.98, '7.5' UNION ALL
    SELECT 'yl8aar3mpx', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'rtx4090d09', 'hourly', 8.50, 7.23, '8.5' UNION ALL
    SELECT 'vgpu32gb32', 'hourly', 2.30, 2.19, '9.5' UNION ALL
    SELECT 'rtx3080ti88', 'hourly', 1.80, 1.71, '9.5' UNION ALL
    SELECT 'cpuonly001', 'hourly', 0.20, 0.19, '9.5'
) p ON r.machine_uuid = p.machine_uuid
ON DUPLICATE KEY UPDATE unit_price = VALUES(unit_price), discount_price = VALUES(discount_price), discount_rate = VALUES(discount_rate);

INSERT INTO gpu_resource_stock (resource_id, available_count, total_count, last_sync_time)
SELECT r.id, s.available_count, s.total_count, NOW()
FROM gpu_resource r
JOIN (
    SELECT '47034ea2ad' machine_uuid, 1 available_count, 9 total_count UNION ALL
    SELECT 'ufu1jbsm98', 0, 8 UNION ALL
    SELECT '58a0ln8rhl', 1, 8 UNION ALL
    SELECT 'f79448928d', 1, 9 UNION ALL
    SELECT '7d414f84a8', 1, 9 UNION ALL
    SELECT 'yl8aar3mpx', 1, 10 UNION ALL
    SELECT 'rtx4090d09', 0, 8 UNION ALL
    SELECT 'vgpu32gb32', 2, 8 UNION ALL
    SELECT 'rtx3080ti88', 3, 8 UNION ALL
    SELECT 'cpuonly001', 2, 2
) s ON r.machine_uuid = s.machine_uuid
ON DUPLICATE KEY UPDATE available_count = VALUES(available_count), total_count = VALUES(total_count), last_sync_time = VALUES(last_sync_time);
