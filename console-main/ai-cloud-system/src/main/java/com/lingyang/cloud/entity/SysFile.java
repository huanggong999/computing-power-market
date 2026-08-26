package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lingyang.common.datasource.model.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_file")
public class SysFile extends BaseEntity {

    /**
     * 類型（1 文件夾， 2 文件）
     */
    @Schema(description = "類型（1 文件夾， 2 文件）")
    private Integer type;

    /**
     * 用户id
     */
    @TableField("user_id")
    @Schema(description = "用户id")
    private Long userId;

    /**
     * 桶id
     */
    @TableField("bucket_id")
    @Schema(description = "桶id")
    private Long bucketId;

    /**
     * 文件名称带文件类型的(xx.txt)/文件夹名称
     */
    @TableField("name")
    @Schema(description = "文件名称带文件类型的(xx.txt)/文件夹名称")
    private String name;


    /**
     * 文件全路徑/文件夹全路径
     */
    @TableField("path")
    @Schema(description = "文件全路徑/文件夹全路径")
    private String path;


    /**
     * 文件夾id(0 就是顶级)
     */
    @TableField("folder_id")
    @Schema(description = "文件夾id(0 就是顶级)")
    private Long folderId;

    /**
     * 文件大小
     */
    @TableField("size")
    @Schema(description = "文件大小")
    private BigDecimal size;


}
