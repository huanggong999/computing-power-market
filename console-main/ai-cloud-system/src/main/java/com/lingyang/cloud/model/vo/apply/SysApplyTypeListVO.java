package com.lingyang.cloud.model.vo.apply;

import com.baomidou.mybatisplus.annotation.TableField;
import com.lingyang.cloud.entity.SysApplyTypeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SysApplyTypeListVO {

    /**
     * 分类id
     */
    @Schema(description = "分类id")
    private Long id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String name;

    /**
     * 子分类列表
     */
    @Schema(description = "子分类列表")
    private List<SysApplyTypeListVO> childrenList;

    public static SysApplyTypeListVO build(SysApplyTypeEntity entity) {
        SysApplyTypeListVO vo = new SysApplyTypeListVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        return vo;
    }

}
