-- ============================================================
-- 后台管理端：新增 AI算力中心 / 火山云GPU资源管理 菜单
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
    '火山云GPU资源管理',
    @gpu_parent_id,
    5,
    'volcanoGpuResource',
    'gpu/volcano/index',
    NULL,
    1,
    0,
    'C',
    0,
    0,
    '',
    'List',
    'admin',
    1,
    NOW(),
    'admin',
    1,
    NOW(),
    '火山云GPU资源维护入口',
    0
WHERE @gpu_parent_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM sys_menu
      WHERE path = 'volcanoGpuResource'
        AND del_flag = 0
  );

-- 如果菜单此前已按旧页面路径插入，则同步切换到专用可售快照页面。
UPDATE sys_menu
SET component = 'gpu/volcano/index',
    menu_type = 'C',
    visible = 0,
    status = 0,
    perms = '',
    update_by = 'admin',
    update_by_id = 1,
    update_time = NOW()
WHERE path = 'volcanoGpuResource'
  AND del_flag = 0;

-- 兼容历史数据：如果菜单曾被错误地挂在内容管理下，按名称修正父级。
UPDATE sys_menu m
JOIN sys_menu p ON p.parent_id = 0
  AND p.path = 'gpu'
  AND p.menu_type = 'M'
  AND p.del_flag = 0
SET m.parent_id = p.id,
    m.component = 'gpu/volcano/index',
    m.menu_type = 'C',
    m.visible = 0,
    m.status = 0,
    m.update_by = 'admin',
    m.update_by_id = 1,
    m.update_time = NOW()
WHERE m.path = 'volcanoGpuResource'
  AND m.menu_name = '火山云GPU资源管理'
  AND m.del_flag = 0;
