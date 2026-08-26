INSERT INTO gpu_resource_spec (model, vram, architecture, description, tags, sort_order, status) VALUES
('RTX 5090', '32 GB', 'Blackwell', 'NVIDIA RTX 5090，顶级消费级显卡', '["热门","最新"]', 1, 1),
('RTX PRO 6000', '96 GB', 'Blackwell', 'NVIDIA RTX PRO 6000，专业级显卡', '["专业级","大显存"]', 2, 1),
('vGPU-32GB', '32 GB', 'Virtual GPU', 'vGPU 32GB 云端虚拟 GPU', '["弹性","虚拟GPU"]', 3, 1),
('vGPU-48GB', '48 GB', 'Virtual GPU', 'vGPU 48GB 云端虚拟 GPU', '["弹性","虚拟GPU"]', 4, 1),
('H800', '80 GB', 'Hopper', 'NVIDIA H800，数据中心级GPU', '["专业级"]', 5, 1),
('RTX 4090D', '24 GB', 'Ada Lovelace', 'NVIDIA RTX 4090D，高性能显卡', '["热门"]', 6, 1),
('RTX 4090', '24 GB', 'Ada Lovelace', 'NVIDIA RTX 4090，高性能显卡', '["热门","高性价比"]', 7, 1),
('RTX 3090', '24 GB', 'Ampere', 'NVIDIA RTX 3090，上一代旗舰', '["高性价比"]', 8, 1),
('RTX 3080x2', '20 GB', 'Ampere', '双 RTX 3080 资源', '["高性价比"]', 9, 1),
('RTX 3080 Ti', '12 GB', 'Ampere', 'NVIDIA RTX 3080 Ti，中高端显卡', '["入门级"]', 10, 1),
('RTX A4000', '16 GB', 'Ampere', 'NVIDIA RTX A4000，专业图形显卡', '["专业级"]', 11, 1),
('RTX 3060', '12 GB', 'Ampere', 'NVIDIA RTX 3060，入门级显卡', '["入门级"]', 12, 1),
('GTX 1080 Ti', '11 GB', 'Pascal', 'NVIDIA GTX 1080 Ti，经典显卡', '["入门级"]', 13, 1),
('CPU', '0 GB', 'CPU', 'CPU 计算资源', '["CPU"]', 14, 1),
('CPU-close-HT', '0 GB', 'CPU', '关闭超线程 CPU 计算资源', '["CPU"]', 15, 1),
('vGPU-48GB-350W', '48 GB', 'Virtual GPU', '350W vGPU 48GB 云端虚拟 GPU', '["弹性","虚拟GPU"]', 16, 1),
('A800', '80 GB', 'Ampere', 'NVIDIA A800，数据中心级GPU', '["专业级"]', 17, 1)
ON DUPLICATE KEY UPDATE
vram = VALUES(vram),
architecture = VALUES(architecture),
description = VALUES(description),
tags = VALUES(tags),
sort_order = VALUES(sort_order),
status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-001', 'C79机', '47034ea2ad', 'northwest-b', NULL, s.id, 1, '580.119.02', '13.0', 1, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '0 GB', '2027-10-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX PRO 6000'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-002', 'D11机', 'ufu1jbsm98', 'northwest-b', NULL, s.id, 1, '580.105.08', '13.0', 1, 25, 'Xeon(R) Platinum 8470Q', '92 GB', '30 GB', '50 GB', '1015 GB', '2027-10-01', 1 FROM gpu_resource_spec s WHERE s.model = 'vGPU-48GB'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-003', 'D12机', '58a0ln8rhl', 'northwest-b', NULL, s.id, 1, '580.105.08', '13.0', 1, 25, 'Xeon(R) Platinum 8470Q', '92 GB', '30 GB', '50 GB', '3512 GB', '2027-10-01', 1 FROM gpu_resource_spec s WHERE s.model = 'vGPU-48GB'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-004', '948机', 'f79448928d', 'northwest-b', NULL, s.id, 1, '580.82.09', '13.0', 0, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '81 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX PRO 6000'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-005', 'B96机', 'yl8aar3mpx', 'northwest-b', NULL, s.id, 1, '580.105.08', '13.0', 1, 12, 'Xeon(R) Gold 6459C 基频3.0G', '96 GB', '30 GB', '50 GB', '3021 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'vGPU-48GB'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-006', 'A17机', 'd15845a561', 'northwest-b', NULL, s.id, 1, '580.95.05', '13.0', 1, 22, 'Xeon(R) Platinum 8470Q', '110 GB', '30 GB', '50 GB', '0 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX PRO 6000'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-007', 'K18机', 'rtx4090d001', 'beijing-b', NULL, s.id, 1, '550.54.15', '12.4', 0, 16, 'Intel Xeon Gold 6330', '64 GB', '30 GB', '50 GB', '500 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX 4090D'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-008', 'K20机', 'rtx4090001', 'beijing-a', NULL, s.id, 1, '560.94', '12.6', 0, 16, 'AMD EPYC 7543', '96 GB', '30 GB', '50 GB', '1200 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX 4090'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-009', 'T88机', 'rtx3080ti001', 'foshan', NULL, s.id, 2, '535.104.05', '12.2', 0, 24, 'Intel Xeon Gold 6230R', '128 GB', '30 GB', '50 GB', '800 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'RTX 3080 Ti'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-010', 'CPU01机', 'cpu001closeht', 'inner-b', NULL, s.id, 1, 'N/A', 'N/A', 0, 32, 'AMD EPYC 7K62', '128 GB', '30 GB', '50 GB', '500 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'CPU'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status)
SELECT 'RES-20260523-011', 'V32机', 'vgpu32gb001', 'chongqing-a', NULL, s.id, 1, '580.105.08', '13.0', 1, 16, 'Xeon(R) Gold 6459C 基频3.0G', '64 GB', '30 GB', '50 GB', '900 GB', '2027-01-01', 1 FROM gpu_resource_spec s WHERE s.model = 'vGPU-32GB'
ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id), region_code = VALUES(region_code), zone_code = VALUES(zone_code), spec_id = VALUES(spec_id), gpu_count = VALUES(gpu_count), status = VALUES(status);

INSERT INTO gpu_resource_price (resource_id, billing_type, unit_price, discount_price, discount_rate, currency)
SELECT r.id, p.billing_type, p.unit_price, p.discount_price, p.discount_rate, 'CNY'
FROM gpu_resource r
JOIN (
    SELECT '47034ea2ad' machine_uuid, 'hourly' billing_type, 7.97 unit_price, 5.98 discount_price, '7.5' discount_rate UNION ALL
    SELECT '47034ea2ad', 'daily', 191.28, 143.46, '7.5' UNION ALL
    SELECT '47034ea2ad', 'weekly', 1338.96, 1004.22, '7.5' UNION ALL
    SELECT '47034ea2ad', 'monthly', 5740.00, 4305.00, '7.5' UNION ALL
    SELECT 'ufu1jbsm98', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'daily', 72.72, 69.08, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'weekly', 509.04, 483.59, '9.5' UNION ALL
    SELECT 'ufu1jbsm98', 'monthly', 2181.60, 2072.52, '9.5' UNION ALL
    SELECT '58a0ln8rhl', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'f79448928d', 'hourly', 7.97, 5.98, '7.5' UNION ALL
    SELECT 'yl8aar3mpx', 'hourly', 3.03, 2.88, '9.5' UNION ALL
    SELECT 'd15845a561', 'hourly', 7.97, 5.98, '7.5' UNION ALL
    SELECT 'rtx4090d001', 'hourly', 8.60, 7.31, '8.5' UNION ALL
    SELECT 'rtx4090001', 'hourly', 8.00, 6.80, '8.5' UNION ALL
    SELECT 'rtx3080ti001', 'hourly', 3.20, 2.72, '8.5' UNION ALL
    SELECT 'cpu001closeht', 'hourly', 0.99, 0.94, '9.5' UNION ALL
    SELECT 'vgpu32gb001', 'hourly', 2.10, 2.00, '9.5'
) p ON r.machine_uuid = p.machine_uuid
ON DUPLICATE KEY UPDATE unit_price = VALUES(unit_price), discount_price = VALUES(discount_price), discount_rate = VALUES(discount_rate), currency = VALUES(currency);

INSERT INTO gpu_resource_stock (resource_id, available_count, total_count, last_sync_time)
SELECT r.id, s.available_count, s.total_count, NOW()
FROM gpu_resource r
JOIN (
    SELECT '47034ea2ad' machine_uuid, 1 available_count, 9 total_count UNION ALL
    SELECT 'ufu1jbsm98', 0, 8 UNION ALL
    SELECT '58a0ln8rhl', 1, 8 UNION ALL
    SELECT 'f79448928d', 1, 9 UNION ALL
    SELECT 'yl8aar3mpx', 1, 10 UNION ALL
    SELECT 'd15845a561', 1, 9 UNION ALL
    SELECT 'rtx4090d001', 0, 8 UNION ALL
    SELECT 'rtx4090001', 1, 8 UNION ALL
    SELECT 'rtx3080ti001', 2, 2 UNION ALL
    SELECT 'cpu001closeht', 2, 2 UNION ALL
    SELECT 'vgpu32gb001', 1, 4
) s ON r.machine_uuid = s.machine_uuid
ON DUPLICATE KEY UPDATE available_count = VALUES(available_count), total_count = VALUES(total_count), last_sync_time = VALUES(last_sync_time);
