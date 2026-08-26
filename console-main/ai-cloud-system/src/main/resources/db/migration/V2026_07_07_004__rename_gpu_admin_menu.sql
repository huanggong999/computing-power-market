-- 后台管理端菜单重命名

UPDATE sys_menu
SET menu_name = 'AI算力中心',
    update_by = 'admin',
    update_by_id = 1,
    update_time = NOW()
WHERE parent_id = 0
  AND path = 'gpu'
  AND menu_type = 'M'
  AND del_flag = 0;

UPDATE sys_menu
SET menu_name = '集群管理',
    update_by = 'admin',
    update_by_id = 1,
    update_time = NOW()
WHERE path = 'gpuCluster'
  AND component = 'gpu/cluster/index'
  AND menu_type = 'C'
  AND del_flag = 0;
