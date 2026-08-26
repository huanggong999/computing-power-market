-- 后台管理端：集群管理拆分为集群列表、节点池管理、组件管理

SET @gpu_parent_id := (
    SELECT id
    FROM sys_menu
    WHERE parent_id = 0
      AND path = 'gpu'
      AND menu_type = 'M'
      AND del_flag = 0
    LIMIT 1
);

SET @gpu_cluster_id := (
    SELECT id
    FROM sys_menu
    WHERE parent_id = @gpu_parent_id
      AND path = 'gpuCluster'
      AND del_flag = 0
    LIMIT 1
);

UPDATE sys_menu
SET menu_name = '集群管理',
    component = NULL,
    menu_type = 'M',
    icon = 'List',
    update_by = 'admin',
    update_by_id = 1,
    update_time = NOW()
WHERE id = @gpu_cluster_id;

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '集群列表', @gpu_cluster_id, 0, 'clusterList', 'gpu/cluster/list/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'clusterList' AND del_flag = 0
  );

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '节点池管理', @gpu_cluster_id, 1, 'nodePool', 'gpu/cluster/nodePool/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'nodePool' AND del_flag = 0
  );

INSERT INTO `sys_menu`
(`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `remark`, `del_flag`)
SELECT
    '组件管理', @gpu_cluster_id, 2, 'component', 'gpu/cluster/component/index', NULL, 1, 0, 'C', 0, 0, '', 'List', 'admin', 1, NOW(), 'admin', 1, NOW(), '', 0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM sys_menu WHERE parent_id = @gpu_cluster_id AND path = 'component' AND del_flag = 0
  );
