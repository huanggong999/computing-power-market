-- ============================================================
-- 为 gpu_resource_spec 表添加 TFLOPS 性能指标字段
-- 对齐 AutoDL 算力市场 GPU 规格展示
-- ============================================================

-- 1. 添加单精度浮点性能字段
ALTER TABLE gpu_resource_spec
    ADD COLUMN IF NOT EXISTS single_precision_tflops VARCHAR(32) COMMENT '单精度浮点性能(TFLOPS)';

-- 2. 添加半精度/Tensor浮点性能字段
ALTER TABLE gpu_resource_spec
    ADD COLUMN IF NOT EXISTS half_precision_tflops VARCHAR(32) COMMENT '半精度/Tensor浮点性能(TFLOPS)';

-- 3. 初始化已有 GPU 规格的 TFLOPS 数据（基于公开规格参数）
UPDATE gpu_resource_spec SET
    single_precision_tflops = CASE model
        WHEN 'RTX 5090'     THEN '82.58'
        WHEN 'RTX PRO 6000' THEN '83.1'
        WHEN 'RTX 4090'     THEN '82.58'
        WHEN 'RTX 3090'     THEN '35.58'
        WHEN 'A800'         THEN '19.5'
        WHEN 'H800'         THEN '67'
        WHEN 'RTX 3080 Ti'  THEN '34.1'
        WHEN 'RTX 3060'     THEN '13.1'
        WHEN 'RTX 4090D'    THEN '77.1'
        WHEN 'RTX 3080x2'   THEN '59.5'
        WHEN 'RTX 6000D'    THEN '61.2'
        WHEN 'RTX 5090 D'   THEN '78.2'
        WHEN 'vGPU-32GB'    THEN NULL
        WHEN 'vGPU-48GB'    THEN NULL
        WHEN 'vGPU-48GB-350W' THEN NULL
        WHEN 'CPU'          THEN NULL
        WHEN 'CPU-close-HT' THEN NULL
        ELSE NULL
    END,
    half_precision_tflops = CASE model
        WHEN 'RTX 5090'     THEN '165.2'
        WHEN 'RTX PRO 6000' THEN '166.2'
        WHEN 'RTX 4090'     THEN '165.2'
        WHEN 'RTX 3090'     THEN '71.0'
        WHEN 'A800'         THEN '312'
        WHEN 'H800'         THEN '989'
        WHEN 'RTX 3080 Ti'  THEN '68.2'
        WHEN 'RTX 3060'     THEN '26.2'
        WHEN 'RTX 4090D'    THEN '154.2'
        WHEN 'RTX 3080x2'   THEN '119.0'
        WHEN 'RTX 6000D'    THEN '122.4'
        WHEN 'RTX 5090 D'   THEN '156.4'
        WHEN 'vGPU-32GB'    THEN NULL
        WHEN 'vGPU-48GB'    THEN NULL
        WHEN 'vGPU-48GB-350W' THEN NULL
        WHEN 'CPU'          THEN NULL
        WHEN 'CPU-close-HT' THEN NULL
        ELSE NULL
    END
WHERE single_precision_tflops IS NULL;
