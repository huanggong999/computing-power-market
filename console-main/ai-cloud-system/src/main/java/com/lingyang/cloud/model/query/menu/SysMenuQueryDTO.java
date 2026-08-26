package com.lingyang.cloud.model.query.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/3/27 17:32
 */
@Data
public class SysMenuQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5738267297517126105L;

    @Schema(description = "角色id集合查询")
    private Collection<Long> roleIdList;

    @Schema(description = "菜单id列表查询")
    private Collection<Long> menuIdList;

    @Schema(description = "上级菜单id")
    private Long parentId;
}
