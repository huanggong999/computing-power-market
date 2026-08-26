-- GPU规格扩展：记录从GPU集群节点带出的CPU型号

ALTER TABLE gpu_resource_spec
    ADD COLUMN cpu_model VARCHAR(128) NULL COMMENT 'CPU型号' AFTER cpu_total_cores;
