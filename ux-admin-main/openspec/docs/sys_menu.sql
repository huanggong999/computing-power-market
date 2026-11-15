-- ============================================================
-- AI算力中心菜单 SQL (sys_menu)
-- 表: sys_menu
-- 最后ID: 76，新ID从 77 开始，新增到 85
-- 如果 77-81 已经存在但左侧菜单缺少 集群管理，可单独执行第 6 段。
-- ============================================================

-- 1. 父菜单：AI算力中心 (目录 M)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(77, 'AI算力中心', 0, 6, 'gpu', NULL, NULL, 1, 0, 'M', 0, 0, '', 'Monitor', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 2. 子菜单：GPU资源管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(78, 'GPU资源管理', 77, 0, 'gpuResource', 'gpu/resource/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 3. 子菜单：GPU规格管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(79, 'GPU规格管理', 77, 1, 'gpuSpec', 'gpu/spec/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 4. 子菜单：地区管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(80, '地区管理', 77, 2, 'gpuRegion', 'gpu/region/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 5. 子菜单：专区管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(81, '专区管理', 77, 3, 'gpuZone', 'gpu/zone/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 6. 子菜单：集群管理 (目录 M)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(82, '集群管理', 77, 4, 'gpuCluster', NULL, NULL, 1, 0, 'M', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 7. 集群管理子菜单：集群列表 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(83, '集群列表', 82, 0, 'clusterList', 'gpu/cluster/list/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 8. 集群管理子菜单：节点池管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(84, '节点池管理', 82, 1, 'nodePool', 'gpu/cluster/nodePool/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);

-- 9. 集群管理子菜单：组件管理 (菜单 C)
INSERT INTO `sys_menu`
(`id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
VALUES
(85, '组件管理', 82, 2, 'component', 'gpu/cluster/component/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0);
