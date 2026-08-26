package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.common.login.model.SysLoginUserInfo;
import com.lingyang.cloud.entity.SysMenuEntity;
import com.lingyang.cloud.model.query.menu.SysMenuQueryDTO;
import com.lingyang.cloud.model.vo.menu.RouterVo;
import com.lingyang.cloud.model.vo.menu.SysMenuListVO;
import com.lingyang.cloud.model.vo.menu.SysMenuTree;
import com.lingyang.cloud.api.service.system.SystemMenuService;
import com.lingyang.common.core.model.Tree;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.core.security.SecurityContext;
import com.lingyang.common.core.utils.BeanUtils;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.common.log.enums.BusinessType;
import com.lingyang.common.web.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:03
 */
@RestController
@Tag(name = "后台系统-菜单相关")
@RequestMapping("/system/menu")
public class SystemMenuController extends BaseController<SystemMenuService, SysMenuQueryDTO, SysMenuEntity, SysMenuListVO, SysMenuListVO> {

    @Operation(summary = "获取列表")
    @GetMapping("/list")
//    // @PreAuthorize("@ss.hasPermission('system:menu:list')")
    public Result<List<SysMenuListVO>> getList(SysMenuQueryDTO sysMenuQuery) {
        if (ObjectUtils.isEmpty(sysMenuQuery.getParentId())) {
            sysMenuQuery.setParentId(0L);
        }
        return Result.success(service.getList(sysMenuQuery));
    }

    @Operation(summary = "获取树")
    @GetMapping("/tree")
    // @PreAuthorize("@ss.hasPermission('system:menu:tree')")
    public Result<List<SysMenuTree>> getTree(SysMenuQueryDTO sysMenuQuery) {
        return Result.success(Tree.buildTree(BeanUtils.copyList(service.getList(sysMenuQuery), SysMenuTree.class)));
    }

    @Operation(summary = "获取路由信息")
    @GetMapping("/getRouter")
    public Result<List<RouterVo>> getRouter() {
        SysMenuQueryDTO sysMenuListDTO = new SysMenuQueryDTO();
        SysLoginUserInfo userInfo = SecurityContext.getUserInfo();
        if (!SecurityContext.isAdmin()) {
            sysMenuListDTO.setMenuIdList(userInfo.getMenuIdList());
        }
        return Result.success(RouterVo.getVueRouterVo(Tree.buildTree(BeanUtils.copyList(service.getList(sysMenuListDTO), SysMenuTree.class))));
    }


    @Override
    // @PreAuthorize("@ss.hasPermission('system:menu:detail')")
    public Result<SysMenuListVO> detail(Long id) {
        return super.detail(id);
    }

    @Log(businessType = BusinessType.INSERT)
    // @PreAuthorize("@ss.hasPermission('system:menu:save')")
    @Override
    public Result<Void> save(@RequestBody @Valid SysMenuEntity t) {
        return super.save(t);
    }


    @Log(businessType = BusinessType.UPDATE)
    // @PreAuthorize("@ss.hasPermission('system:menu:update')")
    @Override
    public Result<Void> update(@RequestBody SysMenuEntity t) {
        return super.update(t);
    }

    @Log(businessType = BusinessType.DELETE)
    // @PreAuthorize("@ss.hasPermission('system:menu:remove')")
    @Override
    public Result<Void> remove(Long id) {
        return super.remove(id);
    }
}
