CREATE TABLE IF NOT EXISTS volcano_gpu_catalog_snapshot (
    id             BIGINT NOT NULL AUTO_INCREMENT COMMENT '目录快照ID',
    billing_scope  VARCHAR(16) NOT NULL DEFAULT 'all' COMMENT '计费范围 all/on_demand/hourly/monthly',
    catalog_json   LONGTEXT NOT NULL COMMENT '聚合目录JSON',
    source         VARCHAR(32) DEFAULT NULL COMMENT '目录来源',
    refresh_time   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '刷新时间',
    create_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_billing_scope (billing_scope),
    INDEX idx_refresh_time (refresh_time)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '火山云GPU目录快照表';
