package com.lingyang.cloud.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Description:
 * @Author: 吴思镇
 * @Date: 2025/2/20 15:13
 */
@Data
@TableName("sys_image")
public class SysImageEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 6459891529580738182L;
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("id")
    @Schema(description = "主键")
    private Long id;

    @TableField("type")
    @Schema(description = "操作系统类型（veLinux，Ubuntu）")
    private String type;

    @TableField("image_name")
    @Schema(description = "镜像名称")
    private String imageName;

    @TableField("image_id")
    @Schema(description = "镜像id")
    private String imageId;

    @TableField("supported_specifications")
    @Schema(description = "支持的规格")
    private String supportedSpecifications;
}
