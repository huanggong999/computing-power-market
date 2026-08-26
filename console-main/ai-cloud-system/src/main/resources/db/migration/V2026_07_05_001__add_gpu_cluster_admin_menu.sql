-- ============================================================
-- 后台管理端：补齐 AI算力中心 / 集群管理菜单
-- ============================================================

SET @gpu_parent_id := (
    SELECT id
    FROM sys_menu
    WHERE parent_id = 0
      AND path = 'gpu'
      AND menu_type = 'M'
    LIMIT 1
);

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    'AI算力中心', 0, 6, 'gpu', NULL, NULL, 1, 0, 'M', 0, 0, '', 'Monitor', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_parent_id IS NULL;

SET @gpu_parent_id := (
    SELECT id
    FROM sys_menu
    WHERE parent_id = 0
      AND path = 'gpu'
      AND menu_type = 'M'
    LIMIT 1
);

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '集群管理', @gpu_parent_id, 4, 'gpuCluster', NULL, NULL, 1, 0, 'M', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_parent_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE parent_id = @gpu_parent_id
        AND path = 'gpuCluster'
  );

SET @gpu_cluster_id := (
    SELECT id
    FROM sys_menu
    WHERE parent_id = @gpu_parent_id
      AND path = 'gpuCluster'
    LIMIT 1
);

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '集群列表', @gpu_cluster_id, 0, 'clusterList', 'gpu/cluster/list/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'clusterList'
  );

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '节点池管理', @gpu_cluster_id, 1, 'nodePool', 'gpu/cluster/nodePool/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'nodePool'
  );

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '组件管理', @gpu_cluster_id, 2, 'component', 'gpu/cluster/component/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'component'
  );
