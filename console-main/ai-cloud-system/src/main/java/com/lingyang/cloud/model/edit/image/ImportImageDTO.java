package com.lingyang.cloud.model.edit.image;

import com.google.gson.annotations.SerializedName;
import com.volcengine.ecs.model.TagForImportImageInput;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ImportImageDTO {

    @Schema(description = "镜像的架构类型。取值：\n" +
            "amd64：x86计算\n" +
            "arm64：ARM计算")
    private String architecture = null;
    @Schema(description = "镜像的启动模式。取值：\n" +
            "\n" +
            "BIOS（默认）：BIOS启动模式\n" +
            "UEFI：UEFI启动模式")
    private String bootMode = null;
    @Schema(description = "镜像描述。\n" +
            "必须以字母、汉字开头。\n" +
            "只能包含中文，字母，数字，下划线“_”，中划线“-”，等号“=”，英文逗号“,”和英文句号“.”，中文逗号“，”和中文句号“。”和空格。\n" +
            "长度限制为0 ~ 255个字符。\n" +
            "不填默认为空。")
    private String description = null;
    @Schema(description = "镜像名称。\n" +
            "必须以字母、汉字开头。\n" +
            "只能包含中文，字母，数字，下划线“_”，中划线“-”，英文句号“.”。\n" +
            "长度限制为1 ~ 128个字符。")
    private String imageName = null;
    @Schema(description = "操作系统类型。取值：\n" +
            "Linux\n" +
            "Windows")
    private String osType = null;
    @Schema(description = "镜像操作系统的发行版本。取值：\n" +
            "CentOS\n" +
            "Debian\n" +
            "veLinux\n" +
            "Windows Server\n" +
            "Fedora\n" +
            "OpenSUSE\n" +
            "Ubuntu\n" +
            "Rocky Linux\n" +
            "AlmaLinux")
    private String platform = null;
    @Schema(description = "镜像的发行版本。最大长度为100个字符。 7.6")
    private String platformVersion = null;
    @Schema(description = "自定义镜像在TOS存储桶的Url链接。请将自定义镜像文件上传至TOS存储桶获取链接，详情可查看导入自定义镜像。")
    private String url = null;

}
