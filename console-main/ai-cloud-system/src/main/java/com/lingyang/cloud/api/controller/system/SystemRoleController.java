package com.lingyang.cloud.api.controller.system;

import com.lingyang.cloud.model.edit.role.SysRoleEditDTO;
import com.lingyang.cloud.model.query.role.SysRoleQueryDTO;
import com.lingyang.cloud.model.vo.role.SysRoleDetailVO;
import com.lingyang.cloud.model.vo.role.SysRoleListVO;
import com.lingyang.cloud.api.service.system.SystemRoleService;
import com.lingyang.common.core.model.result.PageResult;
import com.lingyang.common.core.model.result.Result;
import com.lingyang.common.log.annotation.Log;
import com.lingyang.common.log.enums.BusinessType;
import com.lingyang.common.web.controller.BaseController;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: 王小龙
 * @Date: 2024/10/21 15:00
 */
@RestController
@Tag(name = "后台系统-角色相关")
@RequestMapping("/system/role")
public class SystemRoleController extends BaseController<SystemRoleService, SysRoleQueryDTO, SysRoleEditDTO, SysRoleListVO, SysRoleDetailVO> {

    @Override
    // @PreAuthorize("@ss.hasPermission('system:role:page')")
    public Result<PageResult<SysRoleListVO>> page(SysRoleQueryDTO sysRoleQueryDTO) {
        return super.page(sysRoleQueryDTO);
    }

    @Override
    // @PreAuthorize("@ss.hasPermission('system:role:detail')")
    public Result<SysRoleDetailVO> detail(Long id) {
        return super.detail(id);
    }

    @Override
    @Log(businessType = BusinessType.INSERT)
    // @PreAuthorize("@ss.hasPermission('system:role:save')")
    public Result<Void> save(@RequestBody @Valid SysRoleEditDTO t) {
        return super.save(t);
    }


    @Override
    @Log(businessType = BusinessType.UPDATE)
    // @PreAuthorize("@ss.hasPermission('system:role:update')")
    public Result<Void> update(@RequestBody SysRoleEditDTO t) {
        return super.update(t);
    }

    @Override
    @Log(businessType = BusinessType.DELETE)
    // @PreAuthorize("@ss.hasPermission('system:role:remove')")
    public Result<Void> remove(Long id) {
        return super.remove(id);
    }
}