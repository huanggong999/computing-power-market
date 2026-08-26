-- GPU规格移除单精度/半精度性能字段

ALTER TABLE gpu_resource_spec
    DROP COLUMN single_precision_tflops,
    DROP COLUMN half_precision_tflops;
