-- GPU组件管理：基础镜像结构化字段

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `gpu_component` ADD COLUMN `component_version` varchar(64) NULL COMMENT ''组件版本'' AFTER `component_name`',
        'SELECT 1'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gpu_component'
      AND COLUMN_NAME = 'component_version'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `gpu_component` ADD COLUMN `python_version` varchar(64) NULL COMMENT ''Python版本'' AFTER `component_version`',
        'SELECT 1'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gpu_component'
      AND COLUMN_NAME = 'python_version'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `gpu_component` ADD COLUMN `os_name` varchar(64) NULL COMMENT ''操作系统名称'' AFTER `python_version`',
        'SELECT 1'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gpu_component'
      AND COLUMN_NAME = 'os_name'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `gpu_component` ADD COLUMN `os_version` varchar(64) NULL COMMENT ''操作系统版本'' AFTER `os_name`',
        'SELECT 1'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gpu_component'
      AND COLUMN_NAME = 'os_version'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE `gpu_component` ADD COLUMN `cuda_version` varchar(64) NULL COMMENT ''CUDA版本'' AFTER `os_version`',
        'SELECT 1'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'gpu_component'
      AND COLUMN_NAME = 'cuda_version'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE `gpu_component`
SET
    `component_version` = NULLIF(TRIM(BOTH ':' FROM TRIM(
        CASE
            WHEN LOWER(TRIM(SUBSTRING_INDEX(`base_image`, '+', 1))) LIKE CONCAT(LOWER(`component_name`), '%')
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(`base_image`, '+', 1)), CHAR_LENGTH(`component_name`) + 1)
            ELSE TRIM(SUBSTRING_INDEX(`base_image`, '+', 1))
        END
    )), ''),
    `python_version` = NULLIF(TRIM(
        CASE
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1))) LIKE 'python%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1)), 7)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1))) LIKE 'python%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1)), 7)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1))) LIKE 'python%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1)), 7)
            ELSE `python_version`
        END
    ), ''),
    `os_name` = CASE
        WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1))) LIKE 'ubuntu%' THEN 'Ubuntu'
        WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1))) LIKE 'ubuntu%' THEN 'Ubuntu'
        WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1))) LIKE 'ubuntu%' THEN 'Ubuntu'
        ELSE `os_name`
    END,
    `os_version` = NULLIF(TRIM(
        CASE
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1))) LIKE 'ubuntu%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1)), 7)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1))) LIKE 'ubuntu%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1)), 7)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1))) LIKE 'ubuntu%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1)), 7)
            ELSE `os_version`
        END
    ), ''),
    `cuda_version` = NULLIF(TRIM(
        CASE
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1))) LIKE 'cuda%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 2), '+', -1)), 5)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1))) LIKE 'cuda%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 3), '+', -1)), 5)
            WHEN LOWER(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1))) LIKE 'cuda%'
                THEN SUBSTRING(TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(`base_image`, '+', 4), '+', -1)), 5)
            ELSE `cuda_version`
        END
    ), '')
WHERE `base_image` IS NOT NULL AND `base_image` <> '';
