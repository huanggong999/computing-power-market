CREATE TABLE IF NOT EXISTS cpu_creation_operation (
    operation_id VARCHAR(64) NOT NULL PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    tenant_name VARCHAR(255) NOT NULL,
    request_key VARCHAR(128) NOT NULL,
    request_json LONGTEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    result_json LONGTEXT NULL,
    message TEXT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_cpu_operation_tenant_request (tenant_id, request_key),
    KEY ix_cpu_operation_queue (status, created_at),
    KEY ix_cpu_operation_tenant (tenant_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
