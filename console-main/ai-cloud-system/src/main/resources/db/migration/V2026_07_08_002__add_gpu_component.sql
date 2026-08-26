-- GPU组件管理：基础组件镜像维护表与初始化数据

CREATE TABLE IF NOT EXISTS `gpu_component` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `component_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '组件名称',
    `base_image` varchar(255) COLLATE utf8mb4_general_ci NOT NULL COMMENT '基础镜像',
    `image_address` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '镜像地址',
    `description` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '说明',
    `sort_order` int DEFAULT 0 COMMENT '排序',
    `status` tinyint DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
    `create_by_id` bigint DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
    `update_by_id` bigint DEFAULT NULL COMMENT '更新者ID',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `del_flag` tinyint DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gpu_component_image` (`image_address`),
    KEY `idx_gpu_component_name` (`component_name`),
    KEY `idx_gpu_component_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='GPU基础组件镜像配置表';

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'PyTorch', 'PyTorch 2.8.0 + Python 3.10 +Ubuntu 22.04 + CUDA 12.8', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch-2.8.0-cu12.8:python3.10', '来源：docs/组件.txt', 10, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch-2.8.0-cu12.8:python3.10');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'PyTorch', 'PyTorch 2.8.0 + Python 3.12 +Ubuntu 22.04+ CUDA 12.8', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch-2.8.0-cu12.8:python3.12', '来源：docs/组件.txt', 20, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/pytorch-2.8.0-cu12.8:python3.12');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'TensorFlow', 'TensorFlow 2.9.0 + Python 3.8 + Ubuntu 20.04 + CUDA 11.2', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorflow-2.9.0-cu11.2:python3.8', '来源：docs/组件.txt', 30, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorflow-2.9.0-cu11.2:python3.8');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'Miniconda', 'Miniconda+ubuntu22.04+Python 3.10+cuda 12.8', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py310-cu12.8:python3.10', '来源：docs/组件.txt', 40, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py310-cu12.8:python3.10');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'Miniconda', 'Miniconda+ubuntu22.04+Python 3.10+cuda 13.0', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py310-cu13.0:python3.10', '来源：docs/组件.txt', 50, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py310-cu13.0:python3.10');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'Miniconda', 'Miniconda+ubuntu22.04+Python 3.12+cuda 13.0', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py312-cu13.0:python3.12', '来源：docs/组件.txt', 60, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/miniconda-py312-cu13.0:python3.12');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'tritonserver', 'tritonserver:25.09+ubuntu24.04+Python 3.12+cuda 13.0', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tritonserver:25.09-ubuntu24-python3.12', '来源：docs/组件.txt', 70, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tritonserver:25.09-ubuntu24-python3.12');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'JAX', 'jax:25.04+ubuntu24.04+Python 3.12+cuda 12.9', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/jax:25.04-cuda12.9-python3.12', '来源：docs/组件.txt', 80, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/jax:25.04-cuda12.9-python3.12');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'paddle', 'paddle:2.6.1+ubuntu20.04+Python 3.10+cuda 12.0', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/paddle:2.6.1-gpu-cuda12.0-cudnn8.9-trt8.6', '来源：docs/组件.txt', 90, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/paddle:2.6.1-gpu-cuda12.0-cudnn8.9-trt8.6');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'TensorRT', 'tensorrt:23.09+ubuntu22.04+Python 3.10+cuda 12.2', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorrt:23.09-py3', '来源：docs/组件.txt', 100, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/tensorrt:23.09-py3');

INSERT INTO `gpu_component`
(`component_name`, `base_image`, `image_address`, `description`, `sort_order`, `status`, `create_by`, `create_by_id`, `create_time`, `update_by`, `update_by_id`, `update_time`, `del_flag`)
SELECT 'Gromacs', 'gromacs:2023.2+ubuntu22.04+Python 3.10+cuda 12.1', 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/gromacs:2023.2', '来源：docs/组件.txt', 110, 1, 'admin', 1, NOW(), 'admin', 1, NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM `gpu_component` WHERE `image_address` = 'acs-sjkuu-cn-beijing.cr.volces.com/gpu/base/gromacs:2023.2');
