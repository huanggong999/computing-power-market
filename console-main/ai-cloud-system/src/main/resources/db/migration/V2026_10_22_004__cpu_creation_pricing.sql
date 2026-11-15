ALTER TABLE cpu_creation_operation ADD COLUMN pricing_json LONGTEXT NULL;
ALTER TABLE cpu_creation_operation ADD COLUMN order_id BIGINT NULL;
ALTER TABLE cpu_creation_operation ADD COLUMN settlement_status VARCHAR(32) NOT NULL DEFAULT 'none';
