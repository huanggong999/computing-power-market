-- 新增GPU资源串联：集群来源字段、专区地区归属、资源组件关联

ALTER TABLE `gpu_resource`
    ADD COLUMN `cluster_id` varchar(128) NULL COMMENT '来源GPU集群ID' AFTER `zone_code`,
    ADD COLUMN `cluster_name` varchar(128) NULL COMMENT '来源GPU集群名称' AFTER `cluster_id`,
    ADD COLUMN `cluster_node_name` varchar(128) NULL COMMENT '来源GPU集群节点名称' AFTER `cluster_name`;

ALTER TABLE `gpu_zone`
    ADD COLUMN `region_code` varchar(64) NULL COMMENT '所属地区编码' AFTER `zone_name`,
    ADD KEY `idx_gpu_zone_region_code` (`region_code`);

CREATE TABLE IF NOT EXISTS `gpu_resource_component` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `resource_id` bigint NOT NULL COMMENT '资源ID',
    `component_id` bigint NOT NULL COMMENT '组件ID',
    `component_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组件名称',
    `base_image` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '基础镜像',
    `image_address` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '镜像地址',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
    `create_by_id` bigint DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
    `update_by_id` bigint DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
    PRIMARY KEY (`id`),
    KEY `idx_gpu_resource_component_resource` (`resource_id`),
    KEY `idx_gpu_resource_component_component` (`component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='GPU资源组件镜像关联表';
