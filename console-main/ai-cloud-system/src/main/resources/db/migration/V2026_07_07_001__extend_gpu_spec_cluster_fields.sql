-- GPU规格扩展：记录从GPU集群节点带出的可编辑字段

ALTER TABLE gpu_resource_spec
    ADD COLUMN cluster_node_name VARCHAR(128) NULL COMMENT '来源GPU集群节点名称' AFTER description,
    ADD COLUMN cluster_status VARCHAR(32) NULL COMMENT 'GPU集群节点状态' AFTER cluster_node_name,
    ADD COLUMN gpu_count INT NULL COMMENT 'GPU总量' AFTER cluster_status,
    ADD COLUMN allocated_gpus INT NULL COMMENT '已分配GPU数量' AFTER gpu_count,
    ADD COLUMN available_gpus INT NULL COMMENT '可用GPU数量' AFTER allocated_gpus,
    ADD COLUMN cpu_total_cores DECIMAL(12, 2) NULL COMMENT 'CPU总核数' AFTER available_gpus,
    ADD COLUMN memory_total_gi DECIMAL(12, 2) NULL COMMENT '内存总量(GiB)' AFTER cpu_total_cores,
    ADD COLUMN disk_total_gi DECIMAL(14, 2) NULL COMMENT '磁盘总量(GiB)' AFTER memory_total_gi;
