-- ============================================================
-- 算力市场全新表结构（不复用任何现有表）
-- ============================================================

-- 1. GPU 规格定义表
CREATE TABLE IF NOT EXISTS gpu_resource_spec (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规格ID',
    model           VARCHAR(64) NOT NULL COMMENT 'GPU型号，如 RTX 5090',
    vram            VARCHAR(32) NOT NULL COMMENT '显存大小，如 32 GB',
    architecture    VARCHAR(64) COMMENT '架构，如 Ada Lovelace',
    description     VARCHAR(256) COMMENT '型号描述',
    tags            VARCHAR(256) COMMENT '标签JSON，如 ["热门","高性能"]',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    status          TINYINT(1) DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_model (model)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU规格定义表';

-- 2. GPU 地区配置表
CREATE TABLE IF NOT EXISTS gpu_region (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地区ID',
    region_code     VARCHAR(32) NOT NULL COMMENT '地区编码，如 northwest-b',
    region_name     VARCHAR(64) NOT NULL COMMENT '地区名称，如 西北B区',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    status          TINYINT(1) DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_region_code (region_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU地区配置表';

-- 3. GPU 专区配置表
CREATE TABLE IF NOT EXISTS gpu_zone (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '专区ID',
    zone_code       VARCHAR(32) NOT NULL COMMENT '专区编码，如 v100',
    zone_name       VARCHAR(64) NOT NULL COMMENT '专区名称，如 V100专区',
    sort_order      INT DEFAULT 0 COMMENT '排序',
    status          TINYINT(1) DEFAULT 1 COMMENT '状态 1启用 0禁用',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_zone_code (zone_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU专区配置表';

-- 4. GPU 资源主表
CREATE TABLE IF NOT EXISTS gpu_resource (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '资源ID',
    resource_no     VARCHAR(32) NOT NULL COMMENT '资源编号，如 RES-2024-001',
    machine_id      VARCHAR(32) NOT NULL COMMENT '机器编号，如 D21机',
    machine_uuid    VARCHAR(64) NOT NULL COMMENT '机器唯一标识，如 3y80s00ajm',
    region_code     VARCHAR(32) NOT NULL COMMENT '地区编码',
    zone_code       VARCHAR(32) COMMENT '专区编码',
    spec_id         BIGINT NOT NULL COMMENT '关联规格ID',
    gpu_count       INT NOT NULL DEFAULT 1 COMMENT '单台机器GPU数量',
    gpu_driver      VARCHAR(64) COMMENT 'GPU驱动版本',
    cuda_version    VARCHAR(32) COMMENT 'CUDA版本',
    cache_optimized TINYINT(1) DEFAULT 0 COMMENT '是否缓存优化 0否 1是',
    cpu_cores       INT NOT NULL COMMENT 'CPU核心数',
    cpu_model       VARCHAR(128) NOT NULL COMMENT 'CPU型号全称',
    memory_size     VARCHAR(32) NOT NULL COMMENT '内存大小，如 92 GB',
    system_disk     VARCHAR(32) NOT NULL COMMENT '系统盘',
    data_disk       VARCHAR(32) NOT NULL COMMENT '数据盘',
    expandable      VARCHAR(32) COMMENT '可扩容容量',
    rentable_until  DATE NOT NULL COMMENT '可租用截止日期',
    status          TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态 1上架 2下架 3维护中',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_machine_uuid (machine_uuid),
    INDEX idx_region_code (region_code),
    INDEX idx_zone_code (zone_code),
    INDEX idx_spec_id (spec_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU资源主表';

-- 5. GPU 资源价格表
CREATE TABLE IF NOT EXISTS gpu_resource_price (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '价格ID',
    resource_id     BIGINT NOT NULL COMMENT '关联资源ID',
    billing_type    VARCHAR(16) NOT NULL COMMENT '计费方式 hourly/daily/weekly/monthly',
    unit_price      DECIMAL(10,2) NOT NULL COMMENT '单价',
    discount_price  DECIMAL(10,2) COMMENT '折扣价',
    discount_rate   VARCHAR(10) COMMENT '折扣率',
    currency        VARCHAR(8) DEFAULT 'CNY' COMMENT '币种',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_resource_billing (resource_id, billing_type),
    INDEX idx_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU资源价格表';

-- 6. GPU 资源实时库存表
CREATE TABLE IF NOT EXISTS gpu_resource_stock (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '库存ID',
    resource_id     BIGINT NOT NULL COMMENT '关联资源ID',
    available_count INT NOT NULL DEFAULT 0 COMMENT '空闲GPU数量',
    total_count     INT NOT NULL DEFAULT 0 COMMENT 'GPU总量',
    last_sync_time  DATETIME COMMENT '上次同步时间',
    create_by       VARCHAR(64),
    create_by_id    BIGINT,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_by       VARCHAR(64),
    update_by_id    BIGINT,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    del_flag        TINYINT(1) DEFAULT 0,
    UNIQUE KEY uk_resource_id (resource_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='GPU资源实时库存表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 初始化地区
INSERT INTO gpu_region (region_code, region_name, sort_order) VALUES
('northwest-b', '西北B区', 1),
('beijing-b', '北京B区', 2),
('chongqing-a', '重庆A区', 3),
('inner-b', '内蒙B区', 4),
('beijing-a', '北京A区', 5),
('foshan', '佛山区', 6)
ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- 初始化专区
INSERT INTO gpu_zone (zone_code, zone_name, sort_order) VALUES
('v100', 'V100专区', 1),
('a800', 'A800专区', 2),
('mthreads', '摩尔线程专区', 3),
('huawei', '华为昇腾专区', 4),
('l20', 'L20专区', 5)
ON DUPLICATE KEY UPDATE zone_name = VALUES(zone_name);

-- 初始化GPU规格
INSERT INTO gpu_resource_spec (model, vram, architecture, description, tags) VALUES
('RTX 5090', '32 GB', 'Blackwell', 'NVIDIA RTX 5090，顶级消费级显卡', '["热门","最新"]'),
('RTX PRO 6000', '96 GB', 'Blackwell', 'NVIDIA RTX PRO 6000，专业级显卡', '["专业级","大显存"]'),
('RTX 4090', '24 GB', 'Ada Lovelace', 'NVIDIA RTX 4090，高性能显卡', '["热门","高性价比"]'),
('RTX 3090', '24 GB', 'Ampere', 'NVIDIA RTX 3090，上一代旗舰', '["高性价比"]'),
('A800', '80 GB', 'Ampere', 'NVIDIA A800，数据中心级GPU', '["专业级"]'),
('H800', '80 GB', 'Hopper', 'NVIDIA H800，数据中心级GPU', '["专业级"]'),
('RTX 3080 Ti', '12 GB', 'Ampere', 'NVIDIA RTX 3080 Ti，中高端显卡', '["入门级"]'),
('RTX 3060', '12 GB', 'Ampere', 'NVIDIA RTX 3060，入门级显卡', '["入门级"]')
ON DUPLICATE KEY UPDATE vram = VALUES(vram);



-- ====================================================
-- 补充初始化数据
-- ====================================================

-- 1. 补充地区（新增 4 个常用节点，与原有 6 个不冲突）
INSERT INTO gpu_region (region_code, region_name, sort_order) VALUES
                                                                  ('shanghai-a',  '上海A区', 7),
                                                                  ('guangzhou-a', '广州A区', 8),
                                                                  ('shenzhen-a',  '深圳A区', 9),
                                                                  ('hangzhou-a',  '杭州A区', 10)
    ON DUPLICATE KEY UPDATE region_name = VALUES(region_name);

-- 2. 初始化 GPU 资源主表（price / stock 的前置依赖）
--    spec_id 按前置数据插入顺序 1~8；region_code / zone_code 与已有数据对齐
INSERT INTO gpu_resource (resource_no, machine_id, machine_uuid, region_code, zone_code, spec_id, gpu_count, gpu_driver, cuda_version, cache_optimized, cpu_cores, cpu_model, memory_size, system_disk, data_disk, expandable, rentable_until, status) VALUES
                                                                                                                                                                                                                                                           ('RES-20250513-001', 'D21机', '3y80s00ajm', 'northwest-b', 'a800',    5, 8, '535.104.05', '12.2', 1, 128, 'Intel Xeon Platinum 8480+', '2048 GB', '500 GB SSD', '8 TB NVMe',  '16 TB', '2026-12-31', 1),
                                                                                                                                                                                                                                                           ('RES-20250513-002', 'B12机', '8k2m9p0qvn', 'beijing-b',   'v100',    6, 8, '535.104.05', '12.2', 1, 128, 'Intel Xeon Platinum 8480+', '2048 GB', '500 GB SSD', '8 TB NVMe',  '16 TB', '2026-12-31', 1),
                                                                                                                                                                                                                                                           ('RES-20250513-003', 'C08机', 'x7n3b1c4dy', 'chongqing-a', 'l20',     1, 8, '560.94',     '12.6', 0, 64,  'AMD EPYC 9654',             '512 GB',  '1 TB NVMe',  '4 TB NVMe',  '8 TB',  '2026-06-30', 1),
                                                                                                                                                                                                                                                           ('RES-20250513-004', 'F15机', 'p9q2r5s8tw', 'inner-b',     'mthreads', 3, 8, '560.94',     '12.6', 0, 64,  'AMD EPYC 9654',             '512 GB',  '1 TB NVMe',  '4 TB NVMe',  '8 TB',  '2026-06-30', 1),
                                                                                                                                                                                                                                                           ('RES-20250513-005', 'G03机', 'a1b2c3d4e5', 'beijing-a',   'huawei',  2, 4, '560.94',     '12.6', 1, 96,  'Intel Xeon W9-3495X',       '1024 GB', '1 TB NVMe',  '4 TB NVMe',  '8 TB',  '2026-12-31', 2),
                                                                                                                                                                                                                                                           ('RES-20250513-006', 'H07机', 'f6g7h8i9j0', 'foshan',      'v100',    4, 8, '535.104.05', '12.2', 0, 64,  'AMD EPYC 7742',             '256 GB',  '500 GB SSD', '2 TB NVMe',  '4 TB',  '2026-03-31', 1)
    ON DUPLICATE KEY UPDATE machine_id = VALUES(machine_id);

-- 3. 初始化 GPU 资源价格（每条资源提供 hourly / monthly 两种计费）
INSERT INTO gpu_resource_price (resource_id, billing_type, unit_price, discount_price, discount_rate, currency) VALUES
                                                                                                                    (1, 'hourly', 45.00, 38.25,  '85%', 'CNY'),
                                                                                                                    (1, 'monthly', 28000.00, 25200.00, '90%', 'CNY'),
                                                                                                                    (2, 'hourly', 55.00, 46.75,  '85%', 'CNY'),
                                                                                                                    (2, 'monthly', 35000.00, 31500.00, '90%', 'CNY'),
                                                                                                                    (3, 'hourly', 12.00, 10.20,  '85%', 'CNY'),
                                                                                                                    (3, 'monthly', 750.00, 6750.00,  '90%', 'CNY'),
                                                                                                                    (4, 'hourly', 8.00,  6.80,   '85%', 'CNY'),
                                                                                                                    (4, 'monthly', 500.00, 450.00,  '90%', 'CNY'),
                                                                                                                    (5, 'hourly', 25.00, 21.25,  '85%', 'CNY'),
                                                                                                                    (5, 'monthly', 15000.00, 13500.00, '90%', 'CNY'),
                                                                                                                    (6, 'hourly', 5.00,  4.25,   '85%', 'CNY'),
                                                                                                                    (6, 'monthly', 320.00, NULL,  NULL, 'CNY')
    ON DUPLICATE KEY UPDATE unit_price = VALUES(unit_price);

-- 4. 初始化 GPU 资源库存
INSERT INTO gpu_resource_stock (resource_id, available_count, total_count, last_sync_time) VALUES
                                                                                               (1, 3, 8, NOW()),
                                                                                               (2, 5, 8, NOW()),
                                                                                               (3, 2, 8, NOW()),
                                                                                               (4, 6, 8, NOW()),
                                                                                               (5, 0, 4, NOW()),
                                                                                               (6, 4, 8, NOW())
    ON DUPLICATE KEY UPDATE available_count = VALUES(available_count);
