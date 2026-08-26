-- ============================================================
-- 后台管理端：新增 AI算力中心 / 集群管理 / 集群监控 菜单
-- ============================================================

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
      AND menu_type = 'M'
      AND del_flag = 0
    LIMIT 1
);

INSERT INTO `sys_menu`
(
    `menu_name`,
    `parent_id`,
    `order_num`,
    `path`,
    `component`,
    `query`,
    `is_frame`,
    `is_cache`,
    `menu_type`,
    `visible`,
    `status`,
    `perms`,
    `icon`,
    `create_by`,
    `create_by_id`,
    `create_time`,
    `update_by`,
    `update_by_id`,
    `update_time`,
    `remark`,
    `del_flag`
)
SELECT
    '集群监控',
    @gpu_cluster_id,
    3,
    'monitor',
    'gpu/cluster/monitor/index',
    NULL,
    1,
    0,
    'C',
    0,
    0,
    '',
    'Monitor',
    'admin',
    1,
    NOW(),
    'admin',
    1,
    NOW(),
    '',
    0
WHERE @gpu_cluster_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE parent_id = @gpu_cluster_id
        AND path = 'monitor'
        AND del_flag = 0
  );
