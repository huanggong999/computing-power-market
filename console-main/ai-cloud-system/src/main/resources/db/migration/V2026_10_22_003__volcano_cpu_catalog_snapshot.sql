CREATE TABLE IF NOT EXISTS volcano_cpu_catalog_snapshot (
    region_code VARCHAR(64) NOT NULL PRIMARY KEY,
    catalog_json LONGTEXT NOT NULL,
    price_version VARCHAR(64) NOT NULL,
    refreshed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_refresh_result LONGTEXT DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='火山云CPU地域价格目录快照';

